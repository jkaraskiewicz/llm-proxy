package server

import config.AppConfig
import interceptors.CallInterceptor
import io.ktor.client.HttpClient
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.runBlocking
import providers.specs.ProviderSpec
import routes.configureRouting
import tokens.TokenHolder
import utils.logger.Logger

class ServerService(
  private val httpClient: HttpClient,
  private val logger: Logger,
  private val callInterceptors: List<CallInterceptor>,
  private val appConfig: AppConfig,
  private val tokenHolder: TokenHolder,
  private val providerSpec: ProviderSpec,
) {

  fun startServing() {
    embeddedServer(CIO, port = appConfig.serverConfig.port, host = appConfig.serverConfig.host) {
      configureRouting(appConfig, httpClient, logger, callInterceptors)
      printStatus()
      loadStoredTokens()
    }.start(wait = true)
  }

  private fun printStatus() {
    logger.log("llm-proxy starting on http://${appConfig.serverConfig.host}:${appConfig.serverConfig.port}")
  }

  private fun loadStoredTokens() {
    runBlocking {
      val token = tokenHolder.currentToken()
      if (token != null) {
        logger.log("Loaded stored token for ${providerSpec.name.value}")
      } else {
        logger.log("No stored token found for ${providerSpec.name.value}")
        logger.log("Run './llm-proxy ${providerSpec.name.value}' auth to authenticate")
      }
    }
  }
}