package providers.specs

import providers.models.AuthenticationScheme
import providers.models.ProviderName

interface ProviderSpec {
  val name: ProviderName
  val clientId: String
  val tokenRefreshUrl: String
  val authorizationUrl: String
  val redirectUri: String
  val scopes: List<String>
  val authenticationScheme: AuthenticationScheme

  fun getAuthorizationUrl(codeChallenge: String, state: String): String
}
