package di

import config.AppConfig
import config.HostConfig
import config.Protocol
import interceptors.ApiKeyInterceptor
import interceptors.RequestInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module
import providers.ProviderSpec
import providers.ProviderFactory
import tokens.TokenManager
import tokens.TokenStorage
import tokens.FileTokenStorage
import auth.OAuthService
import crypto.PKCE
import io.ktor.client.engine.curl.Curl
import utils.logger.Logger
import utils.logger.NativeLogger

val configModule = module {
  single<AppConfig> { defaultAppConfig }
}

val utilsModule = module {
  single<Logger> { NativeLogger() }
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
    named("token")
  }
}

val managersModule = module {
  single<TokenManager> { TokenManager(get(named("token")), get(), get(), get()) }
}

val domainModule = module {
  single<domain.auth.AuthRepository> { infrastructure.auth.AuthRepositoryImpl(get()) }
  single<domain.auth.AuthService> { infrastructure.auth.AuthServiceImpl(get(named("token")), get(), get(), get()) }
  single<domain.tokens.TokenService> { infrastructure.tokens.TokenServiceImpl(get(), get(), get(), get()) }
}

val applicationModule = module {
  single<application.auth.AuthenticateProviderUseCase> { application.auth.AuthenticateProviderUseCase(get(), get(), get()) }
  single<application.tokens.GetValidTokenUseCase> { application.tokens.GetValidTokenUseCase(get(), get()) }
}

val providersModule = module {
  single<ProviderSpec> { ProviderFactory.getDefaultProvider() }
}

val interceptorsModule = module {
  single<RequestInterceptor> {
    ApiKeyInterceptor(get(), get(), get())
  }
}

val storageModule = module {
  single<infrastructure.storage.FileSystemStorage> { infrastructure.storage.NativeFileSystemStorage(get()) }
}

val authModule = module {
  single<TokenStorage> { FileTokenStorage(get(), get()) }
  single<PKCE> { PKCE() }
  single<OAuthService> { OAuthService(get(named("token")), get(), get(), get()) }
  single<auth.DeviceCodeOAuthService> { auth.DeviceCodeOAuthService(get(named("token")), get(), get()) }
  single<auth.AuthServiceFactory> { auth.AuthServiceFactory() }
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