package xyz.mcmxciv.halauncher.domain.authentication

import android.net.Uri
import xyz.mcmxciv.halauncher.data.authentication.AuthenticationRepository
import xyz.mcmxciv.halauncher.domain.models.AuthenticationResult
import xyz.mcmxciv.halauncher.domain.models.TokenResult
import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    val authenticationUrl: String
        get() = authenticationRepository.authenticationUrl

    suspend fun authenticate(authenticationCode: String): AuthenticationResult {
        return when (val result = authenticationRepository.getToken(authenticationCode)) {
            is TokenResult.Success -> {
                authenticationRepository.createSession(result.token)
                AuthenticationResult.SUCCESS
            }
            is TokenResult.InvalidRequest -> AuthenticationResult.INVALID_REQUEST
            is TokenResult.InactiveUser -> AuthenticationResult.INACTIVE_USER
            is TokenResult.UnknownError -> AuthenticationResult.UNKNOWN_ERROR
        }
    }

    fun getAuthenticationCode(url: String): String? =
        Uri.parse(url).getQueryParameter(AuthenticationRepository.RESPONSE_TYPE)

    fun verifyUrl(url: String): Boolean =
        url.contains(AuthenticationRepository.REDIRECT_URI)
}
