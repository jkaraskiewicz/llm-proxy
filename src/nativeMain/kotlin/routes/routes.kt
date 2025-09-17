package routes

import io.ktor.server.application.Application
import io.ktor.server.routing.*

fun Application.configureRouting() {
  routing {
    route("/v1") {
      messagesRoutes()
      modelsRoutes()
      filesRoutes()
      organizationsRoutes()
      experimentalRoutes()
    }
  }
}
