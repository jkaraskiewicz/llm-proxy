package tokens.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = GrantTypeSerializer::class)
enum class GrantType(val value: String) {
  AUTHORIZATION_CODE("authorization_code"),
  REFRESH_TOKEN("refresh_token"),
  DEVICE_CODE("urn:ietf:params:oauth:grant-type:device_code"),
  CLIENT_CREDENTIALS("client_credentials"),
  UNKNOWN("unknown");

  companion object {
    fun fromValue(value: String): GrantType {
      return entries.find { it.value == value } ?: UNKNOWN
    }
  }
}

object GrantTypeSerializer : KSerializer<GrantType> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("GrantType", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: GrantType) {
    encoder.encodeString(value.value)
  }

  override fun deserialize(decoder: Decoder): GrantType {
    val value = decoder.decodeString()
    return GrantType.fromValue(value)
  }
}