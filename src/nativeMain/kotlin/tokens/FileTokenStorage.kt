package tokens

import infrastructure.storage.FileSystemStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import utils.logger.Logger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class FileTokenStorage(
  private val logger: Logger,
  private val fileSystemStorage: FileSystemStorage,
  private val tokensFilePath: String = ".llm-proxy-tokens.json"
) : TokenStorage {

  @OptIn(ExperimentalEncodingApi::class)
  override suspend fun saveToken(providerName: String, token: AuthToken) {
    try {
      val tokenData = mutableMapOf<String, String>()

      // Load existing tokens if file exists
      try {
        val existingContent = fileSystemStorage.readFile(tokensFilePath)
        if (existingContent.isNotEmpty()) {
          tokenData.putAll(Json.decodeFromString<Map<String, String>>(existingContent))
        }
      } catch (e: Exception) {
        // File doesn't exist or is empty, start fresh
      }

      // Add/update the token for this provider
      val tokenJson = Json.encodeToString(token)
      val encodedToken = Base64.encode(tokenJson.encodeToByteArray())
      tokenData[providerName] = encodedToken

      // Save to file
      val content = Json.encodeToString(tokenData)
      fileSystemStorage.writeFile(tokensFilePath, content)

      logger.log("Token saved for provider: $providerName")
    } catch (e: Exception) {
      logger.error("Failed to save token for provider: $providerName", e)
      throw e
    }
  }

  @OptIn(ExperimentalEncodingApi::class)
  override suspend fun loadToken(providerName: String): AuthToken? {
    return try {
      val content = fileSystemStorage.readFile(tokensFilePath)
      if (content.isEmpty()) return null

      val tokenData = Json.decodeFromString<Map<String, String>>(content)
      val encodedToken = tokenData[providerName] ?: return null

      val tokenJson = Base64.decode(encodedToken).decodeToString()
      Json.decodeFromString<AuthToken>(tokenJson)
    } catch (e: Exception) {
      logger.error("Failed to load token for provider: $providerName", e)
      null
    }
  }

  override suspend fun deleteToken(providerName: String) {
    try {
      val content = fileSystemStorage.readFile(tokensFilePath)
      if (content.isEmpty()) return

      val tokenData = Json.decodeFromString<MutableMap<String, String>>(content)
      tokenData.remove(providerName)

      val updatedContent = Json.encodeToString(tokenData)
      fileSystemStorage.writeFile(tokensFilePath, updatedContent)

      logger.log("Token deleted for provider: $providerName")
    } catch (e: Exception) {
      logger.error("Failed to delete token for provider: $providerName", e)
      throw e
    }
  }
}