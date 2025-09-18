package tokens

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OAuthErrorSerializer::class)
enum class OAuthError(val value: String) {
  AUTHORIZATION_PENDING("authorization_pending"),
  SLOW_DOWN("slow_down"),
  EXPIRED_TOKEN("expired_token"),
  ACCESS_DENIED("access_denied"),
  UNKNOWN("unknown");

  companion object {
    fun fromValue(value: String): OAuthError {
      return entries.find { it.value == value } ?: UNKNOWN
    }
  }
}

object OAuthErrorSerializer : KSerializer<OAuthError> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OAuthError", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: OAuthError) {
    encoder.encodeString(value.value)
  }

  override fun deserialize(decoder: Decoder): OAuthError {
    val value = decoder.decodeString()
    return OAuthError.fromValue(value)
  }
}