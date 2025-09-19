package auth

interface AuthenticationService {
  suspend fun authenticate()
}