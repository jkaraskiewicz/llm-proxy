package domain.auth

import tokens.AuthToken

interface AuthRepository {
  suspend fun saveAuthToken(providerName: String, token: AuthToken)
  suspend fun loadAuthToken(providerName: String): AuthToken?
  suspend fun deleteAuthToken(providerName: String)
  suspend fun isTokenValid(token: AuthToken): Boolean
}