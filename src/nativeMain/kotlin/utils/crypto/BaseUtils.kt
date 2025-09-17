package utils.crypto

import io.ktor.util.encodeBase64

object BaseUtils {
  fun ByteArray.toBase64UrlSafe(): String =
    encodeBase64().replace('+', '-').replace('/', '_').trimEnd('=')
}