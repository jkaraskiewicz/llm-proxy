import auth.AuthenticationService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.types.enum
import config.ProviderType
import di.getAllModules
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import server.ServerService

class LlmProxyCommand() : CliktCommand() {
  private val provider by argument().enum<ProviderType>().default(ProviderType.ANTHROPIC)

  override fun run() {
    configureDI(provider)
  }

  class AuthCommand() : CliktCommand(name = "auth"), KoinComponent {
    private val authenticationService: AuthenticationService by inject()

    override fun run() = runBlocking {
      authenticationService.authenticate()
    }
  }

  class ServeCommand() : CliktCommand(name = "serve"), KoinComponent {
    private val serverService: ServerService by inject()

    override fun run() {
      serverService.startServing()
    }
  }

  private fun configureDI(providerType: ProviderType) {
    startKoin {
      modules(
        getAllModules(providerType)
      )
    }
  }
}