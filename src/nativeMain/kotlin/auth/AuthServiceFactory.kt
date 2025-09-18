package auth

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import providers.ProviderSpec
import providers.anthropic.AnthropicSpec
import providers.copilot.CopilotSpec

sealed interface AuthService

class AnthropicAuthService(
  val oAuthService: OAuthService,
) : AuthService

class CopilotAuthService(
  val deviceCodeOAuthService: DeviceCodeOAuthService,
) : AuthService

class AuthServiceFactory : KoinComponent {
  fun forProvider(provider: ProviderSpec): AuthService {
    return when (provider) {
      is AnthropicSpec -> AnthropicAuthService(get())
      is CopilotSpec -> CopilotAuthService(get())
      else -> throw IllegalArgumentException("Unsupported provider: ${provider.name}")
    }
  }
}
