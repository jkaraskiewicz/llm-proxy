package routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.modelsRoutes() {
  route("/models") {
    // List Models
    get {

    }
    // Get a Model
    get("/{modelId}") {

    }
  }
}