package interceptors.wrappers

import io.ktor.http.Headers
import io.ktor.http.headers
import io.ktor.server.request.ApplicationRequest

class HeaderOverwriteRequestWrapper(
  private val originalRequest: ApplicationRequest,
  private val headerName: String,
  private val headerValue: String,
) : ApplicationRequest by originalRequest {

  override val headers: Headers by lazy {
    headers {
      appendAll(originalRequest.headers)
      set(headerName, headerValue)
    }
  }
}