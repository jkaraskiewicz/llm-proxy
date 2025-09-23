package utils.crypto

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.random.CryptographyRandom
import utils.crypto.StringUtils.toBase64UrlSafe

object PKCE {
  // Generate a secure random string for the verifier
  fun generateVerifier(byteLength: Int = 64): String {
    val randomBytes = CryptographyRandom.nextBytes(byteLength)
    return randomBytes.toBase64UrlSafe()
  }

  // Asynchronously generate the SHA-256 challenge
  suspend fun generateChallenge(verifier: String): String {
    // Get the SHA256 algorithm instance
    val sha256 = CryptographyProvider.Default.get(SHA256)
    // Hash the verifier bytes
    val digest = sha256.hasher().hash(verifier.encodeToByteArray())
    return digest.toBase64UrlSafe()
  }

  // Generate both verifier and challenge at once
  suspend fun generatePKCE(): PKCEData {
    val verifier = generateVerifier()
    val challenge = generateChallenge(verifier)
    return PKCEData(challenge, verifier)
  }

  data class PKCEData(val codeChallenge: String, val codeVerifier: String)
}