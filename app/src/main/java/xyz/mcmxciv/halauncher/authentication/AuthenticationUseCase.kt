package xyz.mcmxciv.halauncher.authentication

import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend fun authenticate(authenticationCode: String) {
        val token = authenticationRepository.getToken(authenticationCode)
        authenticationRepository.saveSession(Session(token))
    }
}
