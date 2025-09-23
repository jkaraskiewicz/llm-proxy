package config

import providers.specs.ProviderSpec
import providers.specs.anthropic.AnthropicSpec
import providers.specs.copilot.CopilotSpec

data class AppConfig(
  val serverConfig: HostConfig,
  val clientConfig: HostConfig,
  val tokensFilePath: String,
)

data class HostConfig(
  val protocol: Protocol,
  val host: String,
  val port: Int,
)

enum class Protocol(val value: String) {
  HTTP("http"),
  HTTPS("https"),
}

enum class ProviderType {
  ANTHROPIC, COPILOT;

  fun getProviderSpec(): ProviderSpec = when (this) {
    ANTHROPIC -> AnthropicSpec()
    COPILOT -> CopilotSpec()
  }
}
