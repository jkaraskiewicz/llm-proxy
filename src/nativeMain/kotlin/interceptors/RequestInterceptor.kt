package interceptors

import io.ktor.server.routing.RoutingRequest

interface RequestInterceptor {
  fun intercept(request: RoutingRequest): RoutingRequest
}