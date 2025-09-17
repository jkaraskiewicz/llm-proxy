package routes

import config.AppConfig
import config.toUrl
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveChannel
import io.ktor.server.request.uri
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
  val appConfig by inject<AppConfig>()
  val client by inject<HttpClient>()

  routing {
    route("{...}") {
      handle {
        val forwardUrl = "${appConfig.clientConfig.toUrl()}${call.request.uri}"
        val response: HttpResponse = client.request(forwardUrl) {
          // Method
          method = call.request.httpMethod

          // Body
          val requestBody = call.receiveChannel()
          setBody(requestBody)

          // Headers
          call.request.headers.forEach { key, values ->
            if (!HttpHeaders.isUnsafe(key)) {
              headers.appendAll(key, values)
            }
          }
        }

        response.headers.forEach { key, value ->
          if (!HttpHeaders.isUnsafe(key)) {
            call.response.headers.append(key, value)
          }
        }

        call.respond(HttpStatusCode.fromValue(response.status.value), response.bodyAsChannel())
      }
    }
  }
}
