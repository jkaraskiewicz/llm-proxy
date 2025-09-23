package tokens.store

import config.AppConfig
import storage.FileSystemStorage
import kotlinx.serialization.json.Json
import utils.logger.Logger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import providers.specs.ProviderSpec
import tokens.types.AuthToken

@OptIn(ExperimentalEncodingApi::class)
class FileTokenStorage(
  private val logger: Logger,
  private val fileSystemStorage: FileSystemStorage,
  providerSpec: ProviderSpec,
  appConfig: AppConfig,
) : TokenStorage {
  private val tokensFilePath = appConfig.tokensFilePath
  private val providerName = providerSpec.name.value

  override suspend fun saveToken(token: AuthToken) {
    val tokenData = mutableMapOf<String, String>()

    // Load existing tokens if file exists
    val existingContent = fileSystemStorage.readFile(tokensFilePath)
    existingContent.onSuccess {
      if (it.isNotEmpty()) {
        tokenData.putAll(Json.decodeFromString<Map<String, String>>(it))
      }
    }

    // Add/update the token for this provider
    val tokenJson = Json.encodeToString(token)
    val encodedToken = Base64.encode(tokenJson.encodeToByteArray())
    tokenData[providerName] = encodedToken

    // Save to file
    val content = Json.encodeToString(tokenData)
    fileSystemStorage.writeFile(tokensFilePath, content).onSuccess {
      logger.log("Token saved for provider: $providerName")
    }.onFailure {
      logger.error("Failed to save token for provider: $providerName", it)
      throw it
    }
  }

  override suspend fun loadToken(): AuthToken? {
    val content = fileSystemStorage.readFile(tokensFilePath).getOrElse {
      logger.error("Failed to load token for provider: $providerName", it)
      return null
    }
    return content.takeUnless { it.isEmpty() }?.let {
      val tokenData = Json.decodeFromString<Map<String, String>>(content)
      val encodedToken = tokenData[providerName] ?: return null

      val tokenJson = Base64.decode(encodedToken).decodeToString()
      Json.decodeFromString<AuthToken>(tokenJson)
    }
  }

  override suspend fun deleteToken() {
    val content = fileSystemStorage.readFile(tokensFilePath).getOrElse {
      logger.error("Failed to delete token for provider: $providerName", it)
      throw it
    }
    return content.takeUnless { it.isEmpty() }?.let {
      val tokenData = Json.decodeFromString<MutableMap<String, String>>(it)
      tokenData.remove(providerName)

      val updatedContent = Json.encodeToString(tokenData)
      fileSystemStorage.writeFile(tokensFilePath, updatedContent)

      logger.log("Token deleted for provider: $providerName")
    } ?: Unit
  }
}