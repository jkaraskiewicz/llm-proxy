# Multi-stage build for Kotlin Native application
FROM gradle:8.5-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle.kts gradle.properties settings.gradle.kts ./

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the native executable
RUN ./gradlew linkReleaseExecutableNative --no-daemon

# Runtime stage
FROM ubuntu:22.04

# Install necessary runtime dependencies
RUN apt-get update && \
    apt-get install -y \
    ca-certificates \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set default PUID and PGID
ARG PUID=1000
ARG PGID=1000

# Create group and user with specified IDs
RUN groupadd -g ${PGID} appgroup && \
    useradd -r -u ${PUID} -g ${PGID} -s /bin/false -m -d /app appuser

# Set working directory
WORKDIR /app

# Copy the built executable from builder stage
COPY --from=builder /app/build/bin/native/releaseExecutable/llm-proxy.kexe /app/llm-proxy

# Make executable
RUN chmod +x /app/llm-proxy

# Change ownership to app user
RUN chown -R appuser:appgroup /app

# Switch to app user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1
