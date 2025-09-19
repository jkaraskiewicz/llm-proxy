package storage

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fwrite
import utils.logger.Logger

class NativeFileSystemStorage(
  private val logger: Logger,
) : FileSystemStorage {

  @OptIn(ExperimentalForeignApi::class)
  override suspend fun readFile(path: String): Result<String> {
    return try {
      memScoped {
        val mode = "r"
        val file = fopen(path, mode)
        if (file == null) return Result.failure(IllegalStateException("File not found: $path"))

        val content = StringBuilder()
        val buffer = ByteArray(1024)

        while (true) {
          val bytesRead = fread(buffer.refTo(0), 1u, buffer.size.toULong(), file).toInt()
          if (bytesRead <= 0) break
          content.append(buffer.sliceArray(0 until bytesRead).decodeToString())
        }

        fclose(file)
        Result.success(content.toString())
      }
    } catch (e: Exception) {
      logger.error("Failed to read file: $path", e)
      Result.failure(e)
    }
  }

  @OptIn(ExperimentalForeignApi::class)
  override suspend fun writeFile(path: String, content: String): Result<Unit> {
    try {
      memScoped {
        val mode = "w"
        val file = fopen(path, mode)
        if (file != null) {
          val bytes = content.encodeToByteArray()
          fwrite(bytes.refTo(0), 1u, bytes.size.toULong(), file)
          fclose(file)
        }
      }
    } catch (e: Exception) {
      logger.error("Failed to write file: $path", e)
      return Result.failure(e)
    }
    return Result.success(Unit)
  }
}