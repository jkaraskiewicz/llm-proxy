package tokens.copilot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCodeRequest(
  @SerialName("client_id")
  val clientId: String,
  @SerialName("scope")
  val scope: String
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
  val interval: Int
)

@Serializable
data class AccessTokenRequest(
  @SerialName("client_id")
  val clientId: String,
  @SerialName("device_code")
  val deviceCode: String,
  @SerialName("grant_type")
  val grantType: String = "urn:ietf:params:oauth:grant-type:device_code"
)

@Serializable
data class AccessTokenResponse(
  @SerialName("access_token")
  val accessToken: String,
  @SerialName("token_type")
  val tokenType: String,
  @SerialName("scope")
  val scope: String
)

@Serializable
data class AccessTokenError(
  @SerialName("error")
  val error: String,
  @SerialName("error_description")
  val errorDescription: String? = null,
  @SerialName("error_uri")
  val errorUri: String? = null
)