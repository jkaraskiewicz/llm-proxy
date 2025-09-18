package infrastructure.tokens

import domain.auth.AuthRepository
import domain.auth.AuthService
import domain.tokens.TokenService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import providers.ProviderSpec
import tokens.AuthToken
import utils.logger.Logger

class TokenServiceImpl(
  private val authRepository: AuthRepository,
  private val authService: AuthService,
  private val providerSpec: ProviderSpec,
  private val logger: Logger
) : TokenService {

  private var currentToken: AuthToken? = null
  private val mutex = Mutex()

  fun setInitialToken(token: AuthToken) {
    this.currentToken = token
  }

  override suspend fun getValidAccessToken(providerName: String): Result<String> {
    return runCatching {
      mutex.withLock {
        val token = currentToken ?: authRepository.loadAuthToken(providerName)

        when {
          token == null -> {
            logger.error("No token found for provider: $providerName")
            throw IllegalStateException("No authentication token available for $providerName")
          }
          authRepository.isTokenValid(token) -> {
            token.access
          }
          else -> {
            refreshTokenIfNeeded(providerName)
              .getOrThrow()
              .access
          }
        }
      }
    }.onFailure { e ->
      logger.error("Failed to get valid access token", e)
    }
  }

  override suspend fun refreshTokenIfNeeded(providerName: String): Result<AuthToken> {
    return runCatching {
      val token = currentToken ?: authRepository.loadAuthToken(providerName)
        ?: throw IllegalStateException("No token found for provider: $providerName")

      if (authRepository.isTokenValid(token)) {
        token
      } else {
        authService.refreshToken(providerSpec, token)
          .getOrThrow()
          .also { refreshedToken ->
            currentToken = refreshedToken
          }
      }
    }.onFailure { e ->
      logger.error("Failed to refresh token for $providerName", e)
    }
  }

  override suspend fun storeToken(providerName: String, token: AuthToken): Result<Unit> {
    return runCatching {
      authRepository.saveAuthToken(providerName, token)
      currentToken = token
      logger.log("Token stored for provider: $providerName")
    }.onFailure { e ->
      logger.error("Failed to store token for provider: $providerName", e)
    }
  }

  override suspend fun revokeToken(providerName: String): Result<Unit> {
    return runCatching {
      authRepository.deleteAuthToken(providerName)
      currentToken = null
      logger.log("Token revoked for provider: $providerName")
    }.onFailure { e ->
      logger.error("Failed to revoke token for provider: $providerName", e)
    }
  }
}