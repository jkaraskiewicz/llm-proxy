package auth

import auth.oauth.DeviceCodeOAuthService
import providers.specs.copilot.CopilotSpec
import utils.logger.Logger

class CopilotAuthenticationService(
  private val deviceCodeOAuthService: DeviceCodeOAuthService,
  private val providerSpec: CopilotSpec,
  private val logger: Logger,
) : AuthenticationService {
  override suspend fun authenticate() {
    logger.log("Starting device code authentication for ${providerSpec.name.value}...")

    // Step 1: Get device code
    val deviceCodeResponse = deviceCodeOAuthService.initiateDeviceCodeAuth()

    // Step 2: Fetch and store access token
    deviceCodeOAuthService.fetchAndStoreAccessToken(
      deviceCodeResponse.deviceCode
    )

    logger.log("Successfully authenticated with ${providerSpec.name.value}!")
    logger.log("You can now run 'llm-proxy serve' to start the proxy server.")
  }
}