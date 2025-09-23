package tokens.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TokenTypeSerializer::class)
enum class TokenType(val value: String) {
  OAUTH("oauth"),
  BEARER("bearer"),
  API_KEY("api_key"),
  UNKNOWN("unknown");

  companion object {
    fun fromValue(value: String): TokenType {
      return entries.find { it.value == value } ?: UNKNOWN
    }
  }
}

object TokenTypeSerializer : KSerializer<TokenType> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TokenType", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: TokenType) {
    encoder.encodeString(value.value)
  }

  override fun deserialize(decoder: Decoder): TokenType {
    val value = decoder.decodeString()
    return TokenType.fromValue(value)
  }
}