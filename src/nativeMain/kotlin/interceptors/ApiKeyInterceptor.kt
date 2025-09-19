package interceptors

import interceptors.wrappers.HeaderOverwriteRequestWrapper
import io.ktor.server.request.ApplicationRequest
import kotlinx.coroutines.runBlocking
import providers.specs.ProviderSpec
import tokens.TokenManager

class ApiKeyInterceptor(
  private val providerSpec: ProviderSpec,
  private val tokenManager: TokenManager,
) : RequestInterceptor {

  override fun intercept(request: ApplicationRequest): ApplicationRequest {
    return runBlocking {
      val token = tokenManager.getValidToken()
      val (headerName, headerValue) = providerSpec.authenticationScheme.formatToken(token)
      HeaderOverwriteRequestWrapper(request, headerName, headerValue)
    }
  }
}