package providers.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ProviderNameSerializer::class)
enum class ProviderName(val value: String) {
  ANTHROPIC("anthropic"),
  COPILOT("copilot"),
  OPENAI("openai"),
  GOOGLE("google"),
  UNKNOWN("unknown");

  companion object {
    fun fromValue(value: String): ProviderName {
      return entries.find { it.value == value } ?: UNKNOWN
    }
  }
}

object ProviderNameSerializer : KSerializer<ProviderName> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("ProviderName", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: ProviderName) {
    encoder.encodeString(value.value)
  }

  override fun deserialize(decoder: Decoder): ProviderName {
    val value = decoder.decodeString()
    return ProviderName.fromValue(value)
  }
}