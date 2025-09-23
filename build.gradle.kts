plugins {
  kotlin("multiplatform") version "2.2.20"
  kotlin("plugin.serialization") version "2.2.20"
}

group = "com.karaskiewicz"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

kotlin {
  val hostOs = System.getProperty("os.name")
  val arch = System.getProperty("os.arch")

  val nativeTarget = when {
    hostOs == "Mac OS X" && arch in listOf("x86_64", "amd64") -> macosX64("native")
    hostOs == "Mac OS X" && arch == "aarch64" -> macosArm64("native")
    hostOs == "Linux" && arch in listOf("x86_64", "amd64") -> linuxX64("native")
    hostOs == "Linux" && arch == "aarch64" -> linuxArm64("native")
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  nativeTarget.apply {
    binaries {
      executable {
        entryPoint = "main"
      }
    }
  }

  sourceSets {
    nativeMain.dependencies {
      // Ktor server
      implementation("io.ktor:ktor-server-core:3.2.3")
      implementation("io.ktor:ktor-server-cio:3.2.3")

      // Ktor client
      implementation("io.ktor:ktor-client-core:3.2.3")
      implementation("io.ktor:ktor-client-content-negotiation:3.2.3")
      implementation("io.ktor:ktor-client-curl:3.2.3")

      // TLS support for Native
      implementation("io.ktor:ktor-network:3.2.3")
      implementation("io.ktor:ktor-network-tls:3.2.3")

      // Ktor logging
      implementation("io.ktor:ktor-client-logging:3.2.3")

      // Ktor serialization
      implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3")

      // Cryptography
      implementation("dev.whyoleg.cryptography:cryptography-core:0.5.0")
      implementation("dev.whyoleg.cryptography:cryptography-provider-optimal:0.5.0")
      implementation("dev.whyoleg.cryptography:cryptography-random:0.5.0")

      // DI
      implementation("io.insert-koin:koin-core:4.1.0")
      implementation("io.insert-koin:koin-ktor:4.1.0")

      // CLI
      implementation("com.github.ajalt.clikt:clikt:5.0.3")
    }
  }

  compilerOptions {
    freeCompilerArgs.add("-Xcontext-parameters")
  }
}
