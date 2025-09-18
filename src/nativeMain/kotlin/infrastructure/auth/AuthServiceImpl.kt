package infrastructure.auth

import crypto.PKCE
import domain.auth.AuthRepository
import domain.auth.AuthService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import providers.ProviderSpec
import tokens.AuthToken
import tokens.GrantType
import tokens.RefreshTokenRequest
import tokens.RefreshTokenResponse
import tokens.TokenType
import utils.logger.Logger
import utils.time.TimeUtils

class AuthServiceImpl(
  private val httpClient: HttpClient,
  private val authRepository: AuthRepository,
  private val logger: Logger,
  private val pkce: PKCE
) : AuthService {

  override suspend fun authenticateWithOAuth(
    provider: ProviderSpec,
    authorizationCode: String,
    codeVerifier: String
  ): Result<AuthToken> {
    return runCatching {
      logger.log("Exchanging authorization code for tokens...")

      val response = httpClient.submitForm(
        url = provider.tokenRefreshUrl,
        formParameters = Parameters.build {
          append("grant_type", GrantType.AUTHORIZATION_CODE.value)
          append("client_id", provider.clientId)
          append("code", authorizationCode)
          append("redirect_uri", "http://localhost:3000/callback")
          append("code_verifier", codeVerifier)
        }
      ).body<RefreshTokenResponse>()

      val token = AuthToken(
        type = TokenType.OAUTH,
        refresh = response.newRefreshToken,
        access = response.newAccessToken,
        expires = TimeUtils.currentTimeInMillis() + (response.expiresIn * 1000)
      )

      authRepository.saveAuthToken(provider.name.value, token)
      logger.log("Authentication successful!")
      token
    }.onFailure { e ->
      logger.error("OAuth authentication failed", e)
    }
  }

  override suspend fun authenticateWithDeviceCode(provider: ProviderSpec): Result<AuthToken> {
    return Result.failure(NotImplementedError("Device code authentication not yet implemented"))
  }

  override suspend fun refreshToken(provider: ProviderSpec, token: AuthToken): Result<AuthToken> {
    return runCatching {
      logger.log("Refreshing token...")

      val response = httpClient.post(provider.tokenRefreshUrl) {
        contentType(ContentType.Application.Json)
        setBody(
          RefreshTokenRequest(
            refreshToken = token.refresh,
            clientId = provider.clientId
          )
        )
      }.body<RefreshTokenResponse>()

      val newToken = AuthToken(
        type = TokenType.OAUTH,
        refresh = response.newRefreshToken,
        access = response.newAccessToken,
        expires = TimeUtils.currentTimeInMillis() + (response.expiresIn * 1000)
      )

      authRepository.saveAuthToken(provider.name.value, newToken)
      logger.log("Token refreshed successfully!")
      newToken
    }.onFailure { e ->
      logger.error("Token refresh failed", e)
    }
  }

  override suspend fun getValidToken(providerName: String): Result<String> {
    return runCatching {
      val token = authRepository.loadAuthToken(providerName)
        ?: throw IllegalStateException("No token found for provider: $providerName")

      if (authRepository.isTokenValid(token)) {
        token.access
      } else {
        throw IllegalStateException("Token is expired for provider: $providerName")
      }
    }.onFailure { e ->
      logger.error("Failed to get valid token", e)
    }
  }

  override suspend fun initiateOAuth(provider: ProviderSpec): Result<Pair<String, String>> {
    return runCatching {
      val (codeChallenge, codeVerifier) = pkce.generatePKCE()
      val state = generateRandomString(32)

      val authUrl = provider.getAuthorizationUrl(codeChallenge, "http://localhost:3000/callback", state)

      logger.log("Please visit the following URL to authorize the application:")
      logger.log(authUrl)

      Pair(codeVerifier, state)
    }.onFailure { e ->
      logger.error("Failed to initiate OAuth", e)
    }
  }

  private fun generateRandomString(length: Int): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
      .map { chars[kotlin.random.Random.nextInt(chars.length)] }
      .joinToString("")
  }
}