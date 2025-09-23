package utils.logger.decorators

import utils.logger.Logger

class ReleaseLoggerDecorator(private val logger: Logger) : Logger by logger {
  override fun debug(message: String) = Unit
}