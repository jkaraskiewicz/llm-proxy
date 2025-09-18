package providers

interface ProviderSpec {
  val name: ProviderName
  val clientId: String
  val tokenRefreshUrl: String
  val authorizationUrl: String
  val scopes: List<String>
  val authenticationScheme: AuthenticationScheme

  fun getAuthorizationUrl(codeChallenge: String, redirectUri: String, state: String): String
}