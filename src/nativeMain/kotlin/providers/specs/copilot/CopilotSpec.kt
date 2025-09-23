package providers.specs.copilot

import io.ktor.http.encodeURLParameter
import providers.models.AuthenticationScheme
import providers.models.ProviderName
import providers.specs.ProviderSpec

class CopilotSpec : ProviderSpec {
  override val name = ProviderName.COPILOT
  override val clientId = "Iv1.b507a08c87ecfe98"
  override val tokenRefreshUrl = "https://github.com/login/oauth/access_token"
  override val authorizationUrl = "https://github.com/login/device/code"
  override val redirectUri = "http://localhost:3000/callback"
  override val scopes = listOf("user:read")
  override val authenticationScheme = AuthenticationScheme.BearerToken

  override fun getAuthorizationUrl(
    codeChallenge: String,
    state: String,
  ): String {
    // GitHub Copilot uses device code flow, not authorization code flow
    // This method isn't used for device flow, but we need to implement it
    val params = listOf(
      "client_id=$clientId",
      "scope=${scopes.joinToString(" ").encodeURLParameter(spaceToPlus = true)}"
    ).joinToString("&")

    return "$authorizationUrl?$params"
  }

  val userAgent = "GitHubCopilotChat/0.26.7"
}
