# Docker Usage Guide

## Quick Start

1. **Build and run the proxy:**
   ```bash
   docker-compose up --build
   ```

2. **Authenticate with your provider:**
   ```bash
   # For Anthropic
   docker-compose exec llm-proxy /app/llm-proxy ANTHROPIC auth

   # For GitHub Copilot
   docker-compose exec llm-proxy /app/llm-proxy COPILOT auth
   ```

3. **Use the proxy:**
   ```bash
   curl -X POST http://localhost:8080/v1/messages \
     -H "Content-Type: application/json" \
     -d '{"model": "claude-3-sonnet", "messages": [{"role": "user", "content": "Hello!"}]}'
   ```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_HOST` | `0.0.0.0` | Server bind address |
| `SERVER_PORT` | `8080` | Server port |
| `SERVER_PROTOCOL` | `http` | Server protocol |
| `CLIENT_HOST` | `api.anthropic.com` | Target API host |
| `CLIENT_PORT` | `443` | Target API port |
| `CLIENT_PROTOCOL` | `https` | Target API protocol |
| `TOKENS_FILE_PATH` | `/app/data/.llm-proxy-tokens.json` | Token storage path |

### Multiple Providers

To run multiple proxy instances for different providers:

1. Copy the example override file:
   ```bash
   cp docker-compose.override.yml.example docker-compose.override.yml
   ```

2. Start specific services:
   ```bash
   # Anthropic on port 8080
   docker-compose up llm-proxy-anthropic

   # Copilot on port 8081
   docker-compose up llm-proxy-copilot

   # All providers
   docker-compose up
   ```

## Authentication

### Initial Setup

Before using the proxy, authenticate with your chosen provider:

```bash
# Anthropic
docker-compose exec llm-proxy /app/llm-proxy ANTHROPIC auth

# Follow the prompts to complete OAuth flow
```

### Token Persistence

Tokens are stored in Docker volumes and persist across container restarts:
- `llm-proxy-data` - Default volume
- `llm-proxy-anthropic-data` - Anthropic-specific tokens
- `llm-proxy-copilot-data` - Copilot-specific tokens

## Development

### Building Locally

```bash
# Build the image
docker build -t llm-proxy .

# Run container
docker run -p 8080:8080 llm-proxy
```

### Debug Mode

```bash
# Run with debug executable
docker-compose run --rm llm-proxy /app/llm-proxy --help
```

## Production Deployment

For production use:

1. **Use specific tags:**
   ```yaml
   services:
     llm-proxy:
       image: your-registry/llm-proxy:v1.0.0
   ```

2. **Configure secrets:**
   ```yaml
   services:
     llm-proxy:
       secrets:
         - llm_proxy_tokens
   ```

3. **Use external volumes:**
   ```yaml
   volumes:
     llm-proxy-data:
       external: true
   ```

4. **Add monitoring:**
   ```yaml
   services:
     llm-proxy:
       labels:
         - "traefik.enable=true"
         - "prometheus.io/scrape=true"
   ```

## Troubleshooting

### Common Issues

1. **Authentication fails:**
   ```bash
   # Check logs
   docker-compose logs llm-proxy

   # Re-authenticate
   docker-compose exec llm-proxy /app/llm-proxy ANTHROPIC auth --force
   ```

2. **Health check fails:**
   ```bash
   # Test health endpoint
   docker-compose exec llm-proxy curl -f http://localhost:8080/health
   ```

3. **Token persistence issues:**
   ```bash
   # Check volume
   docker volume inspect llm-proxy_llm-proxy-data

   # Backup tokens
   docker run --rm -v llm-proxy_llm-proxy-data:/data alpine tar czf - -C /data . > tokens-backup.tar.gz
   ```