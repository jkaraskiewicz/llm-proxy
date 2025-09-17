package cli

import auth.DeviceCodeOAuthService
import auth.OAuthService
import config.AppConfig
import di.applicationModule
import di.authModule
import di.httpModule
import di.interceptorsModule
import di.managersModule
import di.providersModule
import di.utilsModule
import io.ktor.client.engine.curl.Curl
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import providers.ProviderSpec
import providers.anthropic.AnthropicSpec
import providers.copilot.CopilotSpec
import routes.configureRouting
import tokens.FileTokenStorage
import tokens.TokenManager
import tokens.TokenStorage
import utils.logger.Logger
import utils.logger.NativeLogger

object cli {
  suspend fun executeAuth(providerName: String, force: Boolean) {
    val logger = NativeLogger()

    try {
      // Create a minimal setup for auth command
      val httpClient = io.ktor.client.HttpClient(Curl) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
          json()
        }
      }

      val tokenStorage = FileTokenStorage(logger)
      val pkce = crypto.PKCE()
      val oAuthService = OAuthService(httpClient, tokenStorage, logger, pkce)

      when (providerName.lowercase()) {
        "anthropic" -> {
          authenticateAnthropic(logger, httpClient, tokenStorage, force)
        }

        "copilot" -> {
          authenticateCopilot(logger, httpClient, tokenStorage, force)
        }

        else -> {
          logger.error("Unsupported provider: $providerName")
          logger.log("Supported providers: anthropic, copilot")
          httpClient.close()
          return
        }
      }

      httpClient.close()
    } catch (e: Exception) {
      logger.error("Authentication failed", e)
    }
  }

  fun executeServe() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
      configureDI()
      configureRouting()
      printStatus()
      loadStoredTokens()
    }.start(wait = true)
  }

  private suspend fun authenticateAnthropic(
    logger: Logger,
    httpClient: io.ktor.client.HttpClient,
    tokenStorage: tokens.TokenStorage,
    force: Boolean,
  ) {
    val provider = AnthropicSpec()
    val pkce = crypto.PKCE()
    val oAuthService = OAuthService(httpClient, tokenStorage, logger, pkce)

    // Check if we already have a valid token
    if (!force) {
      val existingToken = oAuthService.loadStoredToken(provider.name)
      if (existingToken != null) {
        logger.log("Found existing token for ${provider.name}")
        logger.log("Token expires at: ${existingToken.expires}")
        logger.log("Use '--force' to re-authenticate")
        return
      }
    }

    logger.log("Starting OAuth authentication for ${provider.name}...")

    // Step 1: Initiate OAuth flow
    val (codeVerifier, state) = oAuthService.initiateAuth(provider)

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
    val token = oAuthService.exchangeCodeForToken(provider, authorizationCode, codeVerifier)

    logger.log("Successfully authenticated with ${provider.name}!")
    logger.log("Token expires at: ${token.expires}")
    logger.log("You can now run 'llm-proxy serve' to start the proxy server.")
  }

  private suspend fun authenticateCopilot(
    logger: Logger,
    httpClient: io.ktor.client.HttpClient,
    tokenStorage: tokens.TokenStorage,
    force: Boolean,
  ) {
    val provider = CopilotSpec()
    val deviceOAuthService = DeviceCodeOAuthService(httpClient, tokenStorage, logger)

    // Check if we already have a valid token
    if (!force) {
      val existingToken = deviceOAuthService.loadStoredToken(provider.name)
      if (existingToken != null) {
        logger.log("Found existing token for ${provider.name}")
        logger.log("Use '--force' to re-authenticate")
        return
      }
    }

    logger.log("Starting device code authentication for ${provider.name}...")

    // Step 1: Get device code
    val deviceCodeResponse = deviceOAuthService.initiateDeviceCodeAuth(provider)

    // Step 2: Poll for access token
    val token = deviceOAuthService.pollForAccessToken(
      provider,
      deviceCodeResponse.deviceCode,
      deviceCodeResponse.interval
    )

    logger.log("Successfully authenticated with ${provider.name}!")
    logger.log("You can now run 'llm-proxy serve' to start the proxy server.")
  }

  private fun getProvider(name: String): ProviderSpec? {
    return when (name.lowercase()) {
      "anthropic" -> AnthropicSpec()
      "copilot" -> CopilotSpec()
      else -> null
    }
  }

  private fun Application.configureDI() {
    install(Koin) {
      modules(
        applicationModule, utilsModule, httpModule, interceptorsModule, managersModule,
        providersModule, authModule
      )
    }
  }

  private fun Application.printStatus() {
    val appConfig = get<AppConfig>()
    get<Logger>().run {
      log("llm-proxy starting on http://${appConfig.serverConfig.host}:${appConfig.serverConfig.port}")
    }
  }

  private fun Application.loadStoredTokens() {
    val tokenManager = get<TokenManager>()
    val tokenStorage = get<TokenStorage>()
    val providerSpec = get<ProviderSpec>()
    val logger = get<Logger>()

    try {
      runBlocking {
        val token = tokenStorage.loadToken(providerSpec.name)
        if (token != null) {
          tokenManager.setInitialToken(token)
          logger.log("Loaded stored token for ${providerSpec.name}")
        } else {
          logger.log("No stored token found for ${providerSpec.name}")
          logger.log("Run './llm-proxy auth ${providerSpec.name}' to authenticate")
        }
      }
    } catch (e: Exception) {
      logger.error("Failed to load stored tokens", e)
    }
  }
}
