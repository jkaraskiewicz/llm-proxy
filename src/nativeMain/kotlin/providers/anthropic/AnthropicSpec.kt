package providers.anthropic

import providers.ProviderSpec

class AnthropicSpec: ProviderSpec {
  override val clientId = "9d1c250a-e61b-44d9-88ed-5944d1962f5e"
  override val tokenRefreshUrl = "https://console.anthropic.com/v1/oauth/token"
}