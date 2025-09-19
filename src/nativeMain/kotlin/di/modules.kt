package di

import auth.AnthropicAuthenticationService
import auth.AuthenticationService
import auth.CopilotAuthenticationService
import config.AppConfig
import interceptors.ApiKeyInterceptor
import interceptors.RequestInterceptor
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
import storage.FileSystemStorage
import storage.NativeFileSystemStorage
import io.ktor.client.engine.curl.Curl
import providers.specs.anthropic.AnthropicSpec
import providers.specs.copilot.CopilotSpec
import utils.logger.Logger
import utils.logger.NativeLogger

val configModule = module {
  single<AppConfig> { DEFAULT_APP_CONFIG }
}

val utilsModule = module {
  single<Logger> { NativeLogger() }
}

val storageModule = module {
  single<FileSystemStorage> { NativeFileSystemStorage(get()) }
}

val httpModule = module {
  single<HttpClient> {
    HttpClient(Curl) {
      followRedirects = true
    }
  }
  single<HttpClient> {
    HttpClient(Curl) {
      install(ContentNegotiation) { json() }
    }
  } withOptions {
    named("auth")
  }
  single<RequestInterceptor> {
    ApiKeyInterceptor(get(), get())
  }
}

val tokenModule = module {
  single<TokenManager> { TokenManager(get(named("auth")), get(), get(), get()) }
  single<TokenStorage> { FileTokenStorage(get(), get(), get(), get()) }
}

fun authModule(providerType: ProviderType) = module {
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

fun providersModule(providerType: ProviderType) = module {
  single<AnthropicSpec> { AnthropicSpec() }
  single<CopilotSpec> { CopilotSpec() }
  single<ProviderSpec> { providerType.getProviderSpec() }
}
