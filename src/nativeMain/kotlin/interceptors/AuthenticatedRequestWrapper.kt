package interceptors

import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.Parameters
import io.ktor.server.request.ApplicationRequest
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel

class AuthenticatedRequestWrapper(
  private val originalRequest: ApplicationRequest,
  private val headerName: String,
  private val headerValue: String
) : ApplicationRequest by originalRequest {

  override val headers: Headers by lazy {
    HeadersBuilder().apply {
      // Copy all original headers
      originalRequest.headers.forEach { name, values ->
        appendAll(name, values)
      }
      // Add or replace the authentication header
      set(headerName, headerValue)
    }.build()
  }
}