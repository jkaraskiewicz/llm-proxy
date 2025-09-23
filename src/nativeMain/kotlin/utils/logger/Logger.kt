package utils.logger

interface Logger {
  fun debug(message: String)
  fun log(message: String)
  fun warn(message: String)
  fun error(message: String, throwable: Throwable? = null)
}