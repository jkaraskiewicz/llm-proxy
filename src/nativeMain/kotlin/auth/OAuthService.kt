package auth

import crypto.PKCE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import providers.ProviderSpec
import tokens.TokenStorage
import tokens.AuthToken
import tokens.GrantType
import tokens.TokenType
import utils.logger.Logger
import utils.time.TimeUtils
import kotlin.random.Random

@Serializable
data class TokenExchangeRequest(
  @SerialName("grant_type")
  val grantType: GrantType = GrantType.AUTHORIZATION_CODE,
  @SerialName("client_id")
  val clientId: String,
  @SerialName("code")
  val code: String,
  @SerialName("redirect_uri")
  val redirectUri: String,
  @SerialName("code_verifier")
  val codeVerifier: String,
)

@Serializable
data class TokenExchangeResponse(
  @SerialName("access_token")
  val accessToken: String,
  @SerialName("refresh_token")
  val refreshToken: String,
  @SerialName("expires_in")
  val expiresIn: Long,
  @SerialName("token_type")
  val tokenType: String,
)

class OAuthService(
  private val httpClient: HttpClient,
  private val tokenStorage: TokenStorage,
  private val logger: Logger,
  private val pkce: PKCE
) {

  suspend fun initiateAuth(provider: ProviderSpec, redirectUri: String = "http://localhost:3000/callback"): Pair<String, String> {
    val (codeChallenge, codeVerifier) = pkce.generatePKCE()
    val state = generateRandomString(32)

    val authUrl = provider.getAuthorizationUrl(codeChallenge, redirectUri, state)

    logger.log("Generated authorization URL for ${provider.name}")
    logger.log("Please open the following URL in your browser:")
    logger.log(authUrl)
    logger.log("")
    logger.log("After authorization, you'll be redirected to $redirectUri with a code parameter.")

    return Pair(codeVerifier, state)
  }

  suspend fun exchangeCodeForToken(
    provider: ProviderSpec,
    authorizationCode: String,
    codeVerifier: String,
    redirectUri: String = "http://localhost:3000/callback"
  ): AuthToken {
    val request = TokenExchangeRequest(
      clientId = provider.clientId,
      code = authorizationCode,
      redirectUri = redirectUri,
      codeVerifier = codeVerifier
    )

    val response = httpClient.post(provider.tokenRefreshUrl) {
      contentType(ContentType.Application.Json)
      setBody(request)
    }.body<TokenExchangeResponse>()

    val authToken = AuthToken(
      type = TokenType.OAUTH,
      access = response.accessToken,
      refresh = response.refreshToken,
      expires = TimeUtils.currentTimeInMillis() + (response.expiresIn * 1000)
    )

    // Save token to storage
    tokenStorage.saveToken(provider.name.value, authToken)

    logger.log("Successfully exchanged authorization code for tokens")
    return authToken
  }

  suspend fun loadStoredToken(providerName: String): AuthToken? {
    return tokenStorage.loadToken(providerName)
  }

  private fun generateRandomString(length: Int): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
      .map { chars[Random.nextInt(chars.length)] }
      .joinToString("")
  }
}