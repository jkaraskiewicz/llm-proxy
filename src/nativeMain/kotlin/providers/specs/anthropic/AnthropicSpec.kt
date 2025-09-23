package providers.specs.anthropic

import io.ktor.http.encodeURLParameter
import io.ktor.http.encodeURLPathPart
import io.ktor.http.encodeURLQueryComponent
import providers.models.AuthenticationScheme
import providers.models.ProviderName
import providers.specs.ProviderSpec
import utils.HeaderEntity

class AnthropicSpec : ProviderSpec {
  override val name = ProviderName.ANTHROPIC
  override val clientId = "9d1c250a-e61b-44d9-88ed-5944d1962f5e"
  override val tokenRefreshUrl = "https://console.anthropic.com/v1/oauth/token"
  override val authorizationUrl = "https://claude.ai/oauth/authorize"
  override val redirectUri = "https://console.anthropic.com/oauth/code/callback"
  override val scopes = listOf("org:create_api_key", "user:profile", "user:inference")
  override val authenticationScheme = AuthenticationScheme.BearerToken

  override fun getAuthorizationUrl(
    codeChallenge: String,
    state: String,
  ): String {
    val params = listOf(
      "response_type=code",
      "client_id=$clientId",
      "redirect_uri=${redirectUri.encodeURLQueryComponent(encodeFull = true)}",
      "scope=${scopes.joinToString(" ").encodeURLParameter(spaceToPlus = true)}",
      "code_challenge=$codeChallenge",
      "code_challenge_method=S256",
      "state=$state"
    ).joinToString("&")

    return "$authorizationUrl?$params"
  }

  val STANDARD_HEADERS = listOf(
    HeaderEntity.Header(
      "anthropic-beta",
      "oauth-2025-04-20,claude-code-20250219,interleaved-thinking-2025-05-14,fine-grained-tool-streaming-2025-05-14"
    ),
    HeaderEntity.Header("anthropic-version", "2023-06-01"),
    HeaderEntity.Header("User-Agent", "opencode/0.9.0"),
    HeaderEntity.Header("Host", "api.anthropic.com"),
    HeaderEntity.Header("Accept-Encoding", "gzip, deflate, br, zstd"),
    HeaderEntity.Header("Connection", "keep-alive")
  )
}
