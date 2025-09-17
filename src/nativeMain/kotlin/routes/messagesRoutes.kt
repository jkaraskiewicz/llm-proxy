package routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.messagesRoutes() {
  route("/messages") {
    // Messages
    post {

    }
    // Count Message tokens
    post("/count_tokens") {

    }
    route("/batches") {
      // Create a Message Batch
      post {

      }
      // List Message Batches
      get {

      }
      // Retrieve a Message Batch
      get("/{messageBatchId}") {

      }
      // Retrieve Message Batch Results
      get("/{messageBatchId}/results") {

      }
      // Cancel a Message Batch
      post("/{messageBatchId}/cancel") {

      }
      // Delete a Message Batch
      delete("/{messageBatchId}") {

      }
    }
  }
}