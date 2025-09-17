package tokens

import tokens.anthropic.AuthToken

interface TokenStorage {
  suspend fun saveToken(providerName: String, token: AuthToken)
  suspend fun loadToken(providerName: String): AuthToken?
  suspend fun deleteToken(providerName: String)
}