package routes

import config.AppConfig
import config.toUrl
import interceptors.CallInterceptor
import interceptors.models.toApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.util.appendAll
import utils.logger.Logger
import utils.values

fun Application.configureRouting(
  appConfig: AppConfig,
  client: HttpClient,
  logger: Logger,
  interceptors: List<CallInterceptor>,
) {
  routing {
    route("{...}") {
      handle {
        val forwardUrl = "${appConfig.clientConfig.toUrl()}${call.request.uri}"
        val interceptedCall = with(logger) {
          interceptors.fold(call.toApiCall()) { acc, interceptor -> interceptor.intercept(acc) }
        }

        val response = client.request(forwardUrl) {
          method = call.request.httpMethod
          setBody(interceptedCall.body)
          headers {
            clear()
            appendAll(interceptedCall.headers.values())
          }
        }

        response.headers.forEach { key, values ->
          values.forEach { value ->
            if (!HttpHeaders.isUnsafe(key)) {
              call.response.headers.append(key, value)
            }
          }
        }

        call.respond(response.status, response.bodyAsChannel())
      }
    }
  }
}
