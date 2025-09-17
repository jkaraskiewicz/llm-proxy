package interceptors

import io.ktor.server.routing.RoutingRequest

class ApiKeyInterceptor : RequestInterceptor {
  override fun intercept(request: RoutingRequest): RoutingRequest {
    val apiKey = request.headers["x-api-key"]
    return request
  }
}