package tokens

interface TokenStorage {
  suspend fun saveToken(providerName: String, token: AuthToken)
  suspend fun loadToken(providerName: String): AuthToken?
  suspend fun deleteToken(providerName: String)
}