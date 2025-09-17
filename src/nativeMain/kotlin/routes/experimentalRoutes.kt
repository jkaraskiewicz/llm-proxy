package routes

import io.ktor.client.HttpClient
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.experimentalRoutes() {
  val client by inject<HttpClient>()

  route("/experimental") {
    // Generate a prompt
    post("/generate_prompt") {

    }
    // Improve a prompt
    post("/improve_prompt") {

    }
    // Templatize a prompt
    post("/templatize_prompt") {

    }
  }
}