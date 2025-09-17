package di

import config.AppConfig
import config.HostConfig
import config.Protocol
import interceptors.ApiKeyInterceptor
import interceptors.RequestInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module
import providers.ProviderSpec
import providers.anthropic.AnthropicSpec
import tokens.TokenManager
import utils.logger.Logger
import utils.logger.NativeLogger

val applicationModule = module {
  single<AppConfig> { defaultAppConfig }
}

val utilsModule = module {
  single<Logger> { NativeLogger() }
}

val httpModule = module {
  single<HttpClient> {
    HttpClient(CIO) {
      followRedirects = true
    }
  }
  single<HttpClient> {
    HttpClient(CIO) {
      install(ContentNegotiation) { json() }
    }
  } withOptions {
    named("token")
  }
}

val managersModule = module {
  single<TokenManager> { TokenManager(get(named("token")), get(), get()) }
}

val providersModule = module {
  single<ProviderSpec> { AnthropicSpec() }
}

val interceptorsModule = module {
  single<RequestInterceptor> {
    ApiKeyInterceptor()
  }
}

private val defaultAppConfig = AppConfig(
  serverConfig = HostConfig(
    protocol = Protocol.HTTP,
    host = "0.0.0.0",
    port = 8080,
  ),
  clientConfig = HostConfig(
    protocol = Protocol.HTTPS, host = "api.anthropic.com", port = 443
  ),
)