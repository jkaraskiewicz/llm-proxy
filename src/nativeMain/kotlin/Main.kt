import LlmProxyCommand.*
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
  LlmProxyCommand().subcommands(
    AuthCommand(), ServeCommand()
  ).main(args)
}