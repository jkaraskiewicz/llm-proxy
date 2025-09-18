package providers

import providers.anthropic.AnthropicSpec
import providers.copilot.CopilotSpec

object ProviderFactory {
  fun getProvider(name: String): ProviderSpec? {
    val providerName = ProviderName.fromValue(name.lowercase())
    return getProvider(providerName)
  }

  fun getProvider(name: ProviderName): ProviderSpec? {
    return when (name) {
      ProviderName.ANTHROPIC -> AnthropicSpec()
      ProviderName.COPILOT -> CopilotSpec()
      else -> null
    }
  }

  fun getDefaultProvider(): ProviderSpec {
    return AnthropicSpec()
  }

  fun getSupportedProviders(): List<ProviderName> {
    return listOf(ProviderName.ANTHROPIC, ProviderName.COPILOT)
  }
}