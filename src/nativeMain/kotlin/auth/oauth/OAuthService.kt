package auth.oauth

import config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import providers.specs.ProviderSpec
import tokens.store.TokenStorage
import tokens.types.AuthToken
import tokens.types.TokenType
import utils.logger.Logger
import utils.time.TimeUtils
import tokens.types.TokenExchangeRequest
import tokens.types.TokenExchangeResponse
import utils.crypto.PKCE
import utils.crypto.StringUtils.generateRandomString

class OAuthService(
  private val httpClient: HttpClient,
  private val tokenStorage: TokenStorage,
  private val logger: Logger,
  private val appConfig: AppConfig,
  private val providerSpec: ProviderSpec,
) {
  suspend fun initiateAuth(): Pair<String, String> {
    val (codeChallenge, codeVerifier) = PKCE.generatePKCE()
    val state = generateRandomString(32)

    val authUrl = providerSpec.getAuthorizationUrl(codeChallenge, appConfig.redirectUri, state)

    logger.log("Generated authorization URL for ${providerSpec.name}")
    logger.log("Please open the following URL in your browser:")
    logger.log(authUrl)
    logger.log("")
    logger.log("After authorization, you'll be redirected to ${appConfig.redirectUri} with a code parameter.")

    return Pair(codeVerifier, state)
  }

  suspend fun fetchAndStoreAccessToken(
    authorizationCode: String,
    codeVerifier: String,
  ): AuthToken {
    val request = TokenExchangeRequest(
      clientId = providerSpec.clientId,
      code = authorizationCode,
      redirectUri = appConfig.redirectUri,
      codeVerifier = codeVerifier
    )

    val response = httpClient.post(providerSpec.tokenRefreshUrl) {
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
    tokenStorage.saveToken(authToken)

    logger.log("Successfully exchanged authorization code for tokens")
    return authToken
  }
}