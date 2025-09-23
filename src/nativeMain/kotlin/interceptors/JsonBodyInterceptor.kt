package interceptors

import interceptors.models.ApiCall
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import utils.logger.Logger

class JsonBodyInterceptor(
  private val jsonBodyTransformer: (ApiCall, JsonObject) -> JsonObject,
) : CallInterceptor {

  context(logger: Logger)
  override fun intercept(call: ApiCall): ApiCall {
    if (call.contentType != ContentType.Application.Json) {
      logger.log("Content type is not JSON, skipping body interception.")
      return call
    }

    val requestBody = call.body
    val jsonBody = runCatching { Json.parseToJsonElement(requestBody) as? JsonObject }.getOrNull()
    return if (jsonBody != null) {
      val transformedBody = jsonBodyTransformer(call, jsonBody)
      call.copy(body = Json.encodeToString(JsonObject.serializer(), transformedBody))
    } else {
      call
    }
  }
}