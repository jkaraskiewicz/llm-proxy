# LLM Proxy

A proxy server for LLM providers.

## Usage

```bash
./gradlew build
./build/bin/native/releaseExecutable/llm-proxy.kexe [provider] auth
./build/bin/native/releaseExecutable/llm-proxy.kexe [provider] serve
```

Or with Docker:

```bash
docker-compose up -d
```

## Configuration

Set `PROXY_PORT` and `TOKEN_STORAGE_PATH` as needed.

---

*Some things are better left unexplained.*
