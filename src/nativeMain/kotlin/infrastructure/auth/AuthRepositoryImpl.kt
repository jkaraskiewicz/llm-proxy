package infrastructure.auth

import domain.auth.AuthRepository
import tokens.AuthToken
import tokens.TokenStorage
import tokens.isExpired

class AuthRepositoryImpl(
  private val tokenStorage: TokenStorage
) : AuthRepository {

  override suspend fun saveAuthToken(providerName: String, token: AuthToken) {
    tokenStorage.saveToken(providerName, token)
  }

  override suspend fun loadAuthToken(providerName: String): AuthToken? {
    return tokenStorage.loadToken(providerName)
  }

  override suspend fun deleteAuthToken(providerName: String) {
    tokenStorage.deleteToken(providerName)
  }

  override suspend fun isTokenValid(token: AuthToken): Boolean {
    return !token.isExpired()
  }
}