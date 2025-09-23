package tokens

import kotlinx.coroutines.runBlocking
import tokens.store.TokenStorage
import tokens.types.AuthToken

class TokenHolder(private val tokenStorage: TokenStorage) {

  private var cachedToken: AuthToken? = null

  fun currentToken(): AuthToken? = runBlocking {
    cachedToken ?: tokenStorage.loadToken()
  }

  fun setToken(token: AuthToken) = runBlocking {
    tokenStorage.saveToken(token)
    cachedToken = token
  }
}