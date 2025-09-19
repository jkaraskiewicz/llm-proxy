package config

val DEFAULT_APP_CONFIG = AppConfig(
  serverConfig = HostConfig(
    protocol = Protocol.HTTP,
    host = "0.0.0.0",
    port = 8080,
  ),
  clientConfig = HostConfig(
    protocol = Protocol.HTTPS, host = "api.anthropic.com", port = 443
  ),
  tokensFilePath = ".llm-proxy-tokens.json",
)
