package tokens

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import providers.ProviderSpec
import tokens.anthropic.AuthToken
import tokens.anthropic.RefreshTokenRequest
import tokens.anthropic.RefreshTokenResponse
import tokens.anthropic.isExpired
import utils.logger.Logger
import utils.time.TimeUtils

class TokenManager(
  private val httpClient: HttpClient,
  private val providerSpec: ProviderSpec,
  private val logger: Logger,
  private val tokenStorage: TokenStorage,
) {
  private var currentToken: AuthToken? = null
  private val mutex = Mutex()

  fun setInitialToken(token: AuthToken) {
    this.currentToken = token
  }

  suspend fun getValidToken(): String {
    mutex.withLock {
      val validToken = currentToken?.takeIf { !it.isExpired() } ?: refreshToken()
      return validToken.access
    }
  }

  private suspend fun refreshToken(): AuthToken {
    logger.log("Token is expiring. Refreshing...")

    val oldToken = checkNotNull(this.currentToken, {
      "Cannot refresh a null token."
    })

    val responseResult: Result<RefreshTokenResponse> = runCatching {
      httpClient.post(providerSpec.tokenRefreshUrl) {
        contentType(ContentType.Application.Json)
        setBody(
          RefreshTokenRequest(
            refreshToken = oldToken.refresh,
            clientId = providerSpec.clientId
          )
        )
      }.body()
    }

    responseResult.onFailure {
      logger.error("Error refreshing token.", it)
      throw it
    }

    val response = responseResult.getOrThrow()

    logger.log("Token refreshed successfully! New access token is ready.")

    val newToken = AuthToken(
      type = "oauth",
      refresh = response.newRefreshToken,
      access = response.newAccessToken,
      expires = TimeUtils.currentTimeInMillis() + (response.expiresIn * 1000)
    )

    // Save the refreshed token
    try {
      tokenStorage.saveToken(providerSpec.name, newToken)
      logger.log("Refreshed token saved to storage")
    } catch (e: Exception) {
      logger.error("Failed to save refreshed token", e)
    }

    // Update current token
    this.currentToken = newToken

    return newToken
  }
}