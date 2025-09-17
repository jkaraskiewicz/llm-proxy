package providers

interface ProviderSpec {
  val tokenRefreshUrl: String
  val clientId: String
}