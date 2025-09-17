package routes

import config.AppConfig
import config.toUrl
import interceptors.RequestInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveChannel
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
  val appConfig by inject<AppConfig>()
  val client by inject<HttpClient>()
  val interceptors by inject<List<RequestInterceptor>>()

  routing {
    route("{...}") {
      handle {
        val forwardUrl = "${appConfig.clientConfig.toUrl()}${call.request.uri}"
        val proxiedRequest =
          interceptors.fold(call.request) { request, interceptor -> interceptor.intercept(request) }
        val response = client.request(forwardUrl) {
          // Method
          method = proxiedRequest.httpMethod

          // Body
          setBody(call.receiveChannel())

          // Headers
          proxiedRequest.headers.forEach { key, values ->
            if (!HttpHeaders.isUnsafe(key)) {
              headers.appendAll(key, values)
            }
          }
        }

        response.headers.forEach { key, values ->
          if (!HttpHeaders.isUnsafe(key)) {
            values.forEach { value ->
              call.response.headers.append(key, value)
            }
          }
        }

        call.respond(response.status, response.bodyAsChannel())
      }
    }
  }
}
