package providers

interface ProviderSpec {
  val name: String
  val clientId: String
  val tokenRefreshUrl: String
  val authorizationUrl: String
  val scopes: List<String>

  fun getAuthorizationUrl(codeChallenge: String, redirectUri: String, state: String): String
}