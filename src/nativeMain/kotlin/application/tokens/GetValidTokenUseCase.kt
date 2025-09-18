package application.tokens

import domain.tokens.TokenService
import utils.logger.Logger

class GetValidTokenUseCase(
  private val tokenService: TokenService,
  private val logger: Logger
) {

  suspend fun execute(providerName: String): Result<String> {
    return tokenService.getValidAccessToken(providerName)
  }
}