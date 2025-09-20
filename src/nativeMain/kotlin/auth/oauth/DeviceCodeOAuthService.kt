package auth.oauth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import providers.specs.copilot.CopilotSpec
import tokens.store.TokenStorage
import tokens.types.AccessTokenRequest
import tokens.types.AccessTokenResponse
import tokens.types.AuthToken
import tokens.types.DeviceCodeRequest
import tokens.types.DeviceCodeResponse
import tokens.types.TokenType
import utils.logger.Logger
import utils.time.TimeUtils

class DeviceCodeOAuthService(
  private val httpClient: HttpClient,
  private val tokenStorage: TokenStorage,
  private val logger: Logger,
  private val providerSpec: CopilotSpec,
) {

  suspend fun initiateDeviceCodeAuth(): DeviceCodeResponse {
    logger.log("Making request to: ${providerSpec.authorizationUrl}")

    val request = DeviceCodeRequest(
      clientId = providerSpec.clientId,
      scope = providerSpec.scopes.joinToString("+")
    )

    logger.log("Request payload: client_id=${request.clientId}, scope=${request.scope}")

    val response = runCatching {
      httpClient.post(providerSpec.authorizationUrl) {
        contentType(ContentType.Application.Json)
        headers {
          append("User-Agent", providerSpec.userAgent)
          append("Accept", "application/json")
        }
        setBody(request)
      }.body<DeviceCodeResponse>()
    }.getOrElse {
      logger.error("Error in initiateDeviceCodeAuth: ${it.message}", it)
      throw it
    }

    logger.log("Device code obtained for ${providerSpec.name}")
    logger.log("Please visit: ${response.verificationUri}")
    logger.log("Enter user code: ${response.userCode}")
    logger.log("Waiting for authorization...")

    return response
  }

  suspend fun fetchAndStoreAccessToken(deviceCode: String): AuthToken {
    val request = AccessTokenRequest(
      clientId = providerSpec.clientId,
      deviceCode = deviceCode
    )

    val response = runCatching {
      httpClient.post(providerSpec.tokenRefreshUrl) {
        contentType(ContentType.Application.Json)
        headers {
          append("User-Agent", providerSpec.userAgent)
          append("Accept", "application/json")
        }
        setBody(request)
      }.body<String>()
    }.getOrElse {
      logger.error("Error polling for access token", it)
      throw it
    }

    val accessTokenResponse = Json.decodeFromString<AccessTokenResponse>(response)

    val authToken = AuthToken(
      type = TokenType.BEARER,
      access = accessTokenResponse.accessToken,
      refresh = "", // GitHub doesn't provide refresh tokens for device flow
      expires = TimeUtils.currentTimeInMillis() + (365L * 24 * 60 * 60 * 1000) // 1 year default
    )

    // Save token to storage
    tokenStorage.saveToken(authToken)
    logger.log("Successfully obtained access token for ${providerSpec.name}")

    return authToken
  }
}
