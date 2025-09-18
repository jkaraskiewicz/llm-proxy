package interceptors

import application.tokens.GetValidTokenUseCase
import io.ktor.server.request.ApplicationRequest
import kotlinx.coroutines.runBlocking
import providers.ProviderSpec
import utils.logger.Logger

class ApiKeyInterceptor(
  private val getValidTokenUseCase: GetValidTokenUseCase,
  private val providerSpec: ProviderSpec,
  private val logger: Logger
) : RequestInterceptor {

  override fun intercept(request: ApplicationRequest): ApplicationRequest {
    return runBlocking {
      try {
        // Get a valid access token for the configured provider
        getValidTokenUseCase.execute(providerSpec.name.value)
          .fold(
            onSuccess = { accessToken ->
              logger.log("Adding authentication token for ${providerSpec.name}")
              // Use the provider's authentication scheme to format the token
              val (headerName, headerValue) = providerSpec.authenticationScheme.formatToken(accessToken)
              AuthenticatedRequestWrapper(request, headerName, headerValue)
            },
            onFailure = { error ->
              logger.error("Failed to get valid token for ${providerSpec.name}", error)
              // Return original request if we can't get a token
              // The downstream API will handle the authentication failure
              request
            }
          )
      } catch (e: Exception) {
        logger.error("Error in ApiKeyInterceptor", e)
        request
      }
    }
  }
}