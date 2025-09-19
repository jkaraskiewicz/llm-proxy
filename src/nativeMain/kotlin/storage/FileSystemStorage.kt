package storage

interface FileSystemStorage {
  suspend fun readFile(path: String): Result<String>
  suspend fun writeFile(path: String, content: String): Result<Unit>
}