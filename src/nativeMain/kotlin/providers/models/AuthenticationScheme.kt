package providers.models

sealed interface AuthenticationScheme {
  fun formatToken(accessToken: String): Pair<String, String> // Returns (headerName, headerValue)

  data object BearerToken : AuthenticationScheme {
    override fun formatToken(accessToken: String): Pair<String, String> {
      return "Authorization" to "Bearer $accessToken"
    }
  }

  data object ApiKey : AuthenticationScheme {
    override fun formatToken(accessToken: String): Pair<String, String> {
      return "x-api-key" to accessToken
    }
  }
}