package cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking

class LlmProxyCommand : CliktCommand() {
  init {
    subcommands(AuthCommand(), ServeCommand())
  }

  override fun run() {
    // If no subcommand specified, default to serve
    if (currentContext.invokedSubcommand == null) {
      cli.executeServe()
    }
  }
}

class AuthCommand : CliktCommand() {
  private val provider by argument().default("anthropic")
  private val force by option("--force").flag()

  override fun run() = runBlocking {
    cli.executeAuth(provider, force)
  }
}

class ServeCommand : CliktCommand() {
  override fun run() {
    cli.executeServe()
  }
}