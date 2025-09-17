package routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.filesRoutes() {
  route("/files") {
    // Create a file
    post {

    }
    // List files
    get {

    }
    // Get file metadata
    get("/{fileId}") {

    }
    // Download a file
    get("/{fileId}/content") {

    }
    // Delete a file
    delete("/{fileId}") {

    }
  }
}