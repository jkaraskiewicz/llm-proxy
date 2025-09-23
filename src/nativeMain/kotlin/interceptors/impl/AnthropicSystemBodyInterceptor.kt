package interceptors.impl

import interceptors.CallInterceptor
import interceptors.JsonBodyInterceptor
import interceptors.models.ApiCall
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.put
import utils.logger.Logger

class AnthropicSystemBodyInterceptor : CallInterceptor {

  context(logger: Logger)
  override fun intercept(call: ApiCall): ApiCall {
    val jsonTransformer: (ApiCall, JsonObject) -> JsonObject = { apiCall, jsonObject ->
      val targetUrl = apiCall.url
      if (targetUrl !in TARGET_PATHS) {
        logger.log("Url: $targetUrl not in the list of TARGET_PATHS. Skipping.")
        jsonObject
      } else {
        injectSystemMessage(jsonObject)
      }
    }

    val jsonBodyInterceptor = JsonBodyInterceptor(jsonTransformer)
    return jsonBodyInterceptor.intercept(call)
  }

  private fun injectSystemMessage(body: JsonObject): JsonObject {
    val mutableBody = body.toMutableMap()

    val existingSystem = body["system"]?.jsonArray?.toMutableList() ?: mutableListOf()

    val newSystemArray = buildJsonArray {
      add(SYSTEM_STRUCT_OBJECT)
      existingSystem.forEach { add(it) }
    }

    mutableBody["system"] = newSystemArray

    return JsonObject(mutableBody)
  }

  private companion object {
    val TARGET_PATHS = setOf("/v1/messages", "/v1/messages/batches")

    val SYSTEM_STRUCT_OBJECT = buildJsonObject {
      put("type", "text")
      put("text", "You are Claude Code, Anthropic's official CLI for Claude.")
    }
  }
}