package providers.specs.anthropic

import providers.models.AuthenticationScheme
import providers.models.ProviderName
import providers.specs.ProviderSpec

class AnthropicSpec : ProviderSpec {
  override val name = ProviderName.ANTHROPIC
  override val clientId = "9d1c250a-e61b-44d9-88ed-5944d1962f5e"
  override val tokenRefreshUrl = "https://claude.ai/v1/oauth/token"
  override val authorizationUrl = "https://claude.ai/oauth/authorize"
  override val redirectUri = "https://console.anthropic.com/oauth/code/callback"
  override val scopes = listOf("org:create_api_key", "user:profile", "user:inference")
  override val authenticationScheme = AuthenticationScheme.ApiKey

  override fun getAuthorizationUrl(
    codeChallenge: String,
    state: String,
  ): String {
    val params = listOf(
      "response_type=code",
      "client_id=$clientId",
      "redirect_uri=$redirectUri",
      "scope=${scopes.joinToString(" ")}",
      "code_challenge=$codeChallenge",
      "code_challenge_method=S256",
      "state=$state"
    ).joinToString("&")

    return "$authorizationUrl?$params"
  }
}
