package tokens.store

import tokens.types.AuthToken

interface TokenStorage {
  suspend fun saveToken(token: AuthToken)
  suspend fun loadToken(): AuthToken?
  suspend fun deleteToken()
}