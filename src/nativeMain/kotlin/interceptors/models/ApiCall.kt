package interceptors.models

import io.ktor.http.ContentType
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.routing.RoutingCall
import io.ktor.util.flattenEntries
import utils.HeaderEntity

data class ApiCall(
  val url: String,
  val contentType: ContentType,
  val headers: Set<HeaderEntity.Header>,
  val body: String,
)

suspend fun RoutingCall.toApiCall(): ApiCall {
  val url = this.request.uri
  val contentType = this.request.contentType()
  val headers =
    this.request.headers.flattenEntries().map { (key, value) -> HeaderEntity.Header(key, value) }
      .toSet()
  val body = this.receiveText()
  return ApiCall(url, contentType, headers, body)
}