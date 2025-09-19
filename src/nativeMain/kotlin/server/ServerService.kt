package server

import config.AppConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.runBlocking
import org.koin.ktor.plugin.Koin
import providers.specs.ProviderSpec
import routes.configureRouting
import tokens.TokenManager
import tokens.store.TokenStorage
import utils.logger.Logger

class ServerService(
  private val logger: Logger,
  private val appConfig: AppConfig,
  private val tokenStorage: TokenStorage,
  private val tokenManager: TokenManager,
  private val providerSpec: ProviderSpec,
) {

  fun startServing() {
    embeddedServer(CIO, port = appConfig.serverConfig.port, host = appConfig.serverConfig.host) {
      configureDI()
      configureRouting()
      printStatus()
      loadStoredTokens()
    }.start(wait = true)
  }

  private fun Application.configureDI() {
    install(Koin) {
      modules(
        // TODO: which modules should be installed here if any?
      )
    }
  }

  private fun printStatus() {
    logger.log("llm-proxy starting on http://${appConfig.serverConfig.host}:${appConfig.serverConfig.port}")
  }

  private fun loadStoredTokens() {
    runBlocking {
      val token = tokenStorage.loadToken()
      if (token != null) {
        tokenManager.setInitialToken(token)
        logger.log("Loaded stored token for ${providerSpec.name.value}")
      } else {
        logger.log("No stored token found for ${providerSpec.name.value}")
        logger.log("Run './llm-proxy ${providerSpec.name.value}' auth to authenticate")
      }
    }
  }
}