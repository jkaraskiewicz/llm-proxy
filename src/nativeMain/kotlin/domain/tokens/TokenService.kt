package domain.tokens

import tokens.AuthToken

interface TokenService {
  suspend fun getValidAccessToken(providerName: String): Result<String>
  suspend fun refreshTokenIfNeeded(providerName: String): Result<AuthToken>
  suspend fun storeToken(providerName: String, token: AuthToken): Result<Unit>
  suspend fun revokeToken(providerName: String): Result<Unit>
}