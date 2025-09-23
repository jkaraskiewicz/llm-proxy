package interceptors.impl

import interceptors.CallInterceptor
import interceptors.HeadersInterceptor
import interceptors.models.ApiCall
import kotlinx.coroutines.runBlocking
import providers.specs.ProviderSpec
import tokens.TokenManager
import utils.HeaderEntity
import utils.logger.Logger

// TODO: So far Anthropic interceptor is hardcoded (always provided by DI)
// TODO: In the future, we need to provide implementation for Copilot etc.
class AnthropicApiKeyInterceptor(
  private val providerSpec: ProviderSpec,
  private val tokenManager: TokenManager,
) : CallInterceptor {

  context(logger: Logger)
  override fun intercept(call: ApiCall): ApiCall {
    return runBlocking {
      val token = tokenManager.getValidToken()
      val authorizationHeader = providerSpec.authenticationScheme.formatToken(token)
      val headersToSet = setOf(
        authorizationHeader,
        HeaderEntity.Header(
          "anthropic-beta",
          "oauth-2025-04-20,claude-code-20250219,interleaved-thinking-2025-05-14,fine-grained-tool-streaming-2025-05-14"
        ),
        HeaderEntity.Header("User-Agent", "opencode/0.9.0"),
        HeaderEntity.Header("Host", "api.anthropic.com"),
      )
      val headersToRemove = setOf(
        HeaderEntity.Header("X-Api-Key"),
        HeaderEntity.HeaderPrefix("X-Stainless")
      )
      HeadersInterceptor(headersToSet, headersToRemove).intercept(call)
    }
  }
}