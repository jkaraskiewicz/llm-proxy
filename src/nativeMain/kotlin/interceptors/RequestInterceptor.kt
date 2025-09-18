package interceptors

import io.ktor.server.request.ApplicationRequest

interface RequestInterceptor {
  fun intercept(request: ApplicationRequest): ApplicationRequest
}