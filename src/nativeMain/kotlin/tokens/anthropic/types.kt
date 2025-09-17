package tokens.anthropic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.time.TimeUtils

@Serializable
data class TokenData(
  @SerialName("anthropic")
  val anthropic: AuthToken,
)

@Serializable
data class AuthToken(
  @SerialName("type")
  val type: String,
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
  val grantType: String = "refresh_token",
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

fun AuthToken.isExpired(): Boolean {
  val bufferSeconds = 60L // Extra buffer in seconds before expiration
  val currentTimeMillis = TimeUtils.currentTimeInMillis()
  return expires <= (currentTimeMillis + bufferSeconds * 1000)
}

