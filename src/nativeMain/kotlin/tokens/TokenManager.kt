package tokens

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import providers.specs.ProviderSpec
import tokens.types.AuthToken
import tokens.types.RefreshTokenRequest
import tokens.types.RefreshTokenResponse
import tokens.types.TokenType
import tokens.types.isExpired
import utils.logger.Logger
import utils.time.TimeUtils

class TokenManager(
  private val httpClient: HttpClient,
  private val providerSpec: ProviderSpec,
  private val logger: Logger,
  private val tokenHolder: TokenHolder,
) {
  private val mutex = Mutex()

  suspend fun getValidToken(): String {
    mutex.withLock {
      val validToken = tokenHolder.currentToken()?.takeIf { !it.isExpired() } ?: refreshToken()
      return validToken.access
    }
  }

  private suspend fun refreshToken(): AuthToken {
    logger.log("Token is expiring. Refreshing...")

    val oldToken = checkNotNull(tokenHolder.currentToken(), {
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

    val newToken = AuthToken(
      type = TokenType.OAUTH,
      refresh = response.newRefreshToken,
      access = response.newAccessToken,
      expires = TimeUtils.currentTimeInMillis() + (response.expiresIn * 1000)
    )

    saveNewToken(newToken)

    return newToken
  }

  suspend fun saveNewToken(token: AuthToken) {
    tokenHolder.setToken(token)
  }
}
