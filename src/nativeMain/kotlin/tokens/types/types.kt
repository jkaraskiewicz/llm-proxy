package tokens.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
  @SerialName("type")
  val type: TokenType,
  @SerialName("refresh")
  val refresh: String,
  @SerialName("access")
  val access: String,
  @SerialName("expires")
  val expires: Long,
)

@Serializable
data class RefreshTokenRequest(
  @SerialName("grant_type")
  val grantType: GrantType = GrantType.REFRESH_TOKEN,
  @SerialName("refresh_token")
  val refreshToken: String,
  @SerialName("client_id")
  val clientId: String,
)

@Serializable
data class RefreshTokenResponse(
  @SerialName("refresh_token")
  val newRefreshToken: String,
  @SerialName("access_token")
  val newAccessToken: String,
  @SerialName("expires_in")
  val expiresIn: Long, // Duration in seconds
)

@Serializable
data class DeviceCodeRequest(
  @SerialName("client_id")
  val clientId: String,
  @SerialName("scope")
  val scope: String,
)

@Serializable
data class DeviceCodeResponse(
  @SerialName("device_code")
  val deviceCode: String,
  @SerialName("user_code")
  val userCode: String,
  @SerialName("verification_uri")
  val verificationUri: String,
  @SerialName("expires_in")
  val expiresIn: Int,
  @SerialName("interval")
  val interval: Int,
)

@Serializable
data class AccessTokenRequest(
  @SerialName("client_id")
  val clientId: String,
  @SerialName("device_code")
  val deviceCode: String,
  @SerialName("grant_type")
  val grantType: GrantType = GrantType.DEVICE_CODE,
)

@Serializable
data class AccessTokenResponse(
  @SerialName("access_token")
  val accessToken: String,
  @SerialName("token_type")
  val tokenType: String,
  @SerialName("scope")
  val scope: String,
)

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
  @SerialName("state")
  val state: String,
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
