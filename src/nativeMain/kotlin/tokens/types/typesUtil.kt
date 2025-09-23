package tokens.types

import utils.time.TimeUtils

fun AuthToken.isExpired(): Boolean {
  val bufferSeconds = 60L // Extra buffer in seconds before expiration
  val currentTimeMillis = TimeUtils.currentTimeInMillis()
  return expires <= (currentTimeMillis + bufferSeconds * 1000)
}
