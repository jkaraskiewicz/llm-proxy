package application.auth

import domain.auth.AuthService
import domain.tokens.TokenService
import providers.ProviderSpec
import tokens.AuthToken
import utils.logger.Logger

class AuthenticateProviderUseCase(
  private val authService: AuthService,
  private val tokenService: TokenService,
  private val logger: Logger
) {

  suspend fun executeOAuth(
    provider: ProviderSpec,
    authorizationCode: String,
    codeVerifier: String,
    force: Boolean = false
  ): Result<AuthToken> {
    return runCatching {
      // Check if we already have a valid token
      if (!force) {
        authService.getValidToken(provider.name.value)
          .onSuccess {
            logger.log("Found existing valid token for ${provider.name}")
            throw IllegalStateException("Valid token already exists. Use --force to re-authenticate")
          }
      }

      logger.log("Starting OAuth authentication for ${provider.name}...")

      val token = authService.authenticateWithOAuth(provider, authorizationCode, codeVerifier)
        .getOrThrow()

      tokenService.storeToken(provider.name.value, token)
        .getOrThrow()

      logger.log("Successfully authenticated with ${provider.name}!")
      token
    }.onFailure { e ->
      logger.error("Authentication failed for ${provider.name}", e)
    }
  }

  suspend fun executeDeviceCode(
    provider: ProviderSpec,
    force: Boolean = false
  ): Result<AuthToken> {
    return runCatching {
      if (!force) {
        authService.getValidToken(provider.name.value)
          .onSuccess {
            logger.log("Found existing valid token for ${provider.name}")
            throw IllegalStateException("Valid token already exists. Use --force to re-authenticate")
          }
      }

      logger.log("Starting device code authentication for ${provider.name}...")

      val token = authService.authenticateWithDeviceCode(provider)
        .getOrThrow()

      tokenService.storeToken(provider.name.value, token)
        .getOrThrow()

      logger.log("Successfully authenticated with ${provider.name}!")
      token
    }.onFailure { e ->
      logger.error("Authentication failed for ${provider.name}", e)
    }
  }

  suspend fun initiateOAuth(provider: ProviderSpec): Result<Pair<String, String>> {
    return authService.initiateOAuth(provider)
  }
}