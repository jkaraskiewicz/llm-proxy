package di

import auth.AnthropicAuthenticationService
import auth.AuthenticationService
import auth.CopilotAuthenticationService
import config.AppConfig
import interceptors.CallInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module
import providers.specs.ProviderSpec
import tokens.TokenManager
import tokens.store.TokenStorage
import tokens.store.FileTokenStorage
import auth.oauth.DeviceCodeOAuthService
import auth.oauth.OAuthService
import config.DEFAULT_APP_CONFIG
import config.ProviderType
import interceptors.impl.AnthropicApiKeyInterceptor
import interceptors.impl.AnthropicSystemBodyInterceptor
import storage.FileSystemStorage
import storage.NativeFileSystemStorage
import io.ktor.client.engine.curl.Curl
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import providers.specs.anthropic.AnthropicSpec
import providers.specs.copilot.CopilotSpec
import server.ServerService
import tokens.TokenHolder
import utils.logger.Logger
import utils.logger.NativeLogger
import utils.logger.decorators.DebugLoggerDecorator

private val configModule = module {
  single<AppConfig> { DEFAULT_APP_CONFIG }
}

private val utilsModule = module {
  single<Logger> { DebugLoggerDecorator(NativeLogger()) }
}

private val storageModule = module {
  single<FileSystemStorage> { NativeFileSystemStorage(get()) }
}

private val serverModule = module {
  single<ServerService> {
    ServerService(
      get(),
      get(),
      get(),
      get(),
      get(),
      get(),
    )
  }
}

private val httpModule = module {
  single<HttpClient> {
    HttpClient(Curl) {
      install(Logging) {
        logger = io.ktor.client.plugins.logging.Logger.DEFAULT
        level = LogLevel.NONE
      }
      followRedirects = true
    }
  }
  single<HttpClient> {
    HttpClient(Curl) {
      install(Logging) {
        logger = io.ktor.client.plugins.logging.Logger.DEFAULT
        level = LogLevel.NONE
      }
      install(ContentNegotiation) {
        json(Json {
          ignoreUnknownKeys = true
          encodeDefaults = true
          isLenient = true
        })
      }
    }
  } withOptions {
    named("auth")
  }
  single<List<CallInterceptor>> {
    listOf(AnthropicApiKeyInterceptor(get(), get()), AnthropicSystemBodyInterceptor())
  }
}

private val tokenModule = module {
  single<TokenManager> { TokenManager(get(named("auth")), get(), get(), get()) }
  single<TokenStorage> { FileTokenStorage(get(), get(), get(), get()) }
  single<TokenHolder> { TokenHolder(get()) }
}

private fun authModule(providerType: ProviderType) = module {
  single<AuthenticationService> {
    when (providerType) {
      ProviderType.ANTHROPIC -> AnthropicAuthenticationService(get(), get(), get())
      ProviderType.COPILOT -> CopilotAuthenticationService(get(), get(), get())
    }
  }
  single<OAuthService> {
    OAuthService(
      get(named("auth")),
      get(),
      get(),
      get(),
    )
  }
  single<DeviceCodeOAuthService> {
    DeviceCodeOAuthService(
      get(named("auth")),
      get(),
      get(),
      get(),
    )
  }
}

private fun providersModule(providerType: ProviderType) = module {
  single<AnthropicSpec> { AnthropicSpec() }
  single<CopilotSpec> { CopilotSpec() }
  single<ProviderSpec> { providerType.getProviderSpec() }
}

fun getAllModules(providerType: ProviderType) = listOf(
  configModule,
  utilsModule,
  storageModule,
  serverModule,
  httpModule,
  tokenModule,
  authModule(providerType),
  providersModule(providerType),
)
