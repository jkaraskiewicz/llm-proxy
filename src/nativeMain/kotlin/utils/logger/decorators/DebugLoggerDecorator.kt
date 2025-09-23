package utils.logger.decorators

import utils.logger.Logger

class DebugLoggerDecorator(private val logger: Logger) : Logger by logger