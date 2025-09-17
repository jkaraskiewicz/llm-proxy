package providers

import providers.anthropic.AnthropicSpec
import providers.copilot.CopilotSpec

object ProviderFactory {
  fun getProvider(name: String): ProviderSpec? {
    return when (name.lowercase()) {
      "anthropic" -> AnthropicSpec()
      "copilot" -> CopilotSpec()
      else -> null
    }
  }

  fun getDefaultProvider(): ProviderSpec {
    return AnthropicSpec()
  }

  fun getSupportedProviders(): List<String> {
    return listOf("anthropic", "copilot")
  }
}