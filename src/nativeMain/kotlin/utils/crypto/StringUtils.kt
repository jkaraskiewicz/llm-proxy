package utils.crypto

import io.ktor.util.encodeBase64
import kotlin.random.Random

object StringUtils {
  fun ByteArray.toBase64UrlSafe(): String =
    encodeBase64().replace('+', '-').replace('/', '_').trimEnd('=')

  fun generateRandomString(length: Int): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
      .map { chars[Random.nextInt(chars.length)] }
      .joinToString("")
  }
}