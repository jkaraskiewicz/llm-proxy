package interceptors

import interceptors.models.ApiCall
import io.ktor.http.HttpHeaders
import utils.HeaderEntity
import utils.logger.Logger

class HeadersInterceptor(
  private val headersToSet: Set<HeaderEntity.Header>,
  private val headersToRemove: Set<HeaderEntity> = emptySet(),
) : CallInterceptor {

  context(logger: Logger)
  override fun intercept(call: ApiCall): ApiCall {
    val headers = call.headers.toMutableSet().apply {
      removeAllHeaders(headersToSet)
      addAll(headersToSet)
      removeAllHeaders(headersToRemove)
      removeAllUnsafe()
    }
    return call.copy(headers = headers)
  }

  private fun MutableCollection<HeaderEntity.Header>.removeAllUnsafe() {
    removeAll { HttpHeaders.isUnsafe(it.name) }
  }

  private fun MutableCollection<HeaderEntity.Header>.removeAllHeaders(headersToRemove: Set<HeaderEntity>) {
    val keysToRemove = mutableSetOf<String>()
    for (headerEntity in headersToRemove) {
      for (header in this) {
        if (headerEntity.matches(header.name)) {
          keysToRemove.add(header.name)
        }
      }
    }
    removeAll { it.name in keysToRemove }
  }
}