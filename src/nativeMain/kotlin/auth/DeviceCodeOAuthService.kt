package auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import providers.copilot.CopilotSpec
import tokens.TokenStorage
import tokens.AccessTokenError
import tokens.AccessTokenRequest
import tokens.AccessTokenResponse
import tokens.AuthToken
import tokens.DeviceCodeRequest
import tokens.DeviceCodeResponse
import tokens.OAuthError
import tokens.TokenType
import utils.logger.Logger
import utils.time.TimeUtils

class DeviceCodeOAuthService(
  private val httpClient: HttpClient,
  private val tokenStorage: TokenStorage,
  private val logger: Logger
) {

  suspend fun initiateDeviceCodeAuth(provider: CopilotSpec): DeviceCodeResponse {
    logger.log("Making request to: ${provider.deviceCodeUrl}")

    val request = DeviceCodeRequest(
      clientId = provider.clientId,
      scope = provider.scopes.joinToString(" ")
    )

    logger.log("Request payload: client_id=${request.clientId}, scope=${request.scope}")

    try {
      val response = httpClient.post(provider.deviceCodeUrl) {
        contentType(ContentType.Application.Json)
        headers {
          append("User-Agent", provider.userAgent)
          append("Accept", "application/json")
        }
        setBody(request)
      }.body<DeviceCodeResponse>()

      logger.log("Device code obtained for ${provider.name}")
      logger.log("Please visit: ${response.verificationUri}")
      logger.log("Enter user code: ${response.userCode}")
      logger.log("Waiting for authorization...")

      return response
    } catch (e: Exception) {
      logger.error("Error in initiateDeviceCodeAuth: ${e.message}", e)
      throw e
    }
  }

  suspend fun pollForAccessToken(provider: CopilotSpec, deviceCode: String, interval: Int): AuthToken {
    val request = AccessTokenRequest(
      clientId = provider.clientId,
      deviceCode = deviceCode
    )

    while (true) {
      try {
        val response = httpClient.post(provider.accessTokenUrl) {
          contentType(ContentType.Application.Json)
          headers {
            append("User-Agent", provider.userAgent)
            append("Accept", "application/json")
          }
          setBody(request)
        }

        val responseText = response.body<String>()

        // Try to parse as success response
        try {
          val accessTokenResponse = Json.decodeFromString<AccessTokenResponse>(responseText)

          val authToken = AuthToken(
            type = TokenType.BEARER,
            access = accessTokenResponse.accessToken,
            refresh = "", // GitHub doesn't provide refresh tokens for device flow
            expires = TimeUtils.currentTimeInMillis() + (365L * 24 * 60 * 60 * 1000) // 1 year default
          )

          // Save token to storage
          tokenStorage.saveToken(provider.name.value, authToken)
          logger.log("Successfully obtained access token for ${provider.name}")

          return authToken
        } catch (e: Exception) {
          // Try to parse as error response
          val errorResponse = Json.decodeFromString<AccessTokenError>(responseText)

          when (errorResponse.error) {
            OAuthError.AUTHORIZATION_PENDING -> {
              logger.log("Authorization pending, retrying in ${interval}s...")
              delay(interval * 1000L)
              continue
            }
            OAuthError.SLOW_DOWN -> {
              logger.log("Polling too fast, slowing down...")
              delay((interval + 5) * 1000L)
              continue
            }
            OAuthError.EXPIRED_TOKEN -> {
              logger.error("Device code expired")
              throw Exception("Device code expired. Please restart the authentication process.")
            }
            OAuthError.ACCESS_DENIED -> {
              logger.error("Access denied by user")
              throw Exception("Access denied by user")
            }
            else -> {
              logger.error("OAuth error: ${errorResponse.error.value} - ${errorResponse.errorDescription}")
              throw Exception("OAuth error: ${errorResponse.error.value}")
            }
          }
        }
      } catch (e: Exception) {
        logger.error("Error polling for access token", e)
        throw e
      }
    }
  }

  suspend fun loadStoredToken(providerName: String): AuthToken? {
    return tokenStorage.loadToken(providerName)
  }
}