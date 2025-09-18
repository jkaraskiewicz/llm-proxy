package providers

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

  data class CustomHeader(
    private val headerName: String,
    private val valuePrefix: String = "" // Prefix before the token
  ) : AuthenticationScheme {
    override fun formatToken(accessToken: String): Pair<String, String> {
      return headerName to "$valuePrefix$accessToken"
    }
  }
}