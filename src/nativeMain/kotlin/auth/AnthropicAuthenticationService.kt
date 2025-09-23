package auth

import auth.oauth.OAuthService
import providers.specs.anthropic.AnthropicSpec
import utils.logger.Logger

class AnthropicAuthenticationService(
  private val oAuthService: OAuthService,
  private val logger: Logger,
  private val providerSpec: AnthropicSpec,
) : AuthenticationService {
  override suspend fun authenticate() {
    logger.log("Starting OAuth authentication for ${providerSpec.name.value}...")

    // Step 1: Initiate OAuth flow
    val (codeVerifier, _) = oAuthService.initiateAuth()

    // Step 2: Wait for user input
    logger.log("")
    print("Enter the authorization code from the callback URL: ")
    val authorizationCode = readlnOrNull()?.trim()

    if (authorizationCode.isNullOrEmpty()) {
      logger.error("No authorization code provided")
      return
    }

    // Step 3: Exchange code for tokens
    logger.log("Exchanging authorization code for tokens...")
    oAuthService.fetchAndStoreAccessToken(authorizationCode, codeVerifier)

    logger.log("Successfully authenticated with ${providerSpec.name.value}!")
    logger.log("You can now run 'llm-proxy serve' to start the proxy server.")
  }
}