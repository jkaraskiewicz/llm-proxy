package domain.auth

import providers.ProviderSpec
import tokens.AuthToken

interface AuthService {
  suspend fun authenticateWithOAuth(provider: ProviderSpec, authorizationCode: String, codeVerifier: String): Result<AuthToken>
  suspend fun authenticateWithDeviceCode(provider: ProviderSpec): Result<AuthToken>
  suspend fun refreshToken(provider: ProviderSpec, token: AuthToken): Result<AuthToken>
  suspend fun getValidToken(providerName: String): Result<String>
  suspend fun initiateOAuth(provider: ProviderSpec): Result<Pair<String, String>> // Returns (codeVerifier, state)
}