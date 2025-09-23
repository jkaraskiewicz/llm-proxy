package providers.models

import utils.HeaderEntity

sealed interface AuthenticationScheme {
  fun formatToken(accessToken: String): HeaderEntity.Header

  data object BearerToken : AuthenticationScheme {
    override fun formatToken(accessToken: String): HeaderEntity.Header {
      return HeaderEntity.Header("Authorization", "Bearer $accessToken")
    }
  }

  data object ApiKey : AuthenticationScheme {
    override fun formatToken(accessToken: String): HeaderEntity.Header {
      return HeaderEntity.Header("x-api-key", accessToken)
    }
  }
}