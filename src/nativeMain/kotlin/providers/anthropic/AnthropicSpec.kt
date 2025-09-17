package providers.anthropic

import providers.ProviderSpec

class AnthropicSpec: ProviderSpec {
  override val name = "anthropic"
  override val clientId = "9d1c250a-e61b-44d9-88ed-5944d1962f5e"
  override val tokenRefreshUrl = "https://console.anthropic.com/v1/oauth/token"
  override val authorizationUrl = "https://console.anthropic.com/oauth/authorize"
  override val scopes = listOf("org:create_api_key", "user:profile", "user:inference")

  override fun getAuthorizationUrl(codeChallenge: String, redirectUri: String, state: String): String {
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