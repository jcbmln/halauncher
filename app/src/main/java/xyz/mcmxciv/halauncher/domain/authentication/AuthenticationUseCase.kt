package xyz.mcmxciv.halauncher.domain.authentication

import android.net.Uri
import xyz.mcmxciv.halauncher.data.LocalCache
import xyz.mcmxciv.halauncher.data.authentication.AuthenticationRepository
import xyz.mcmxciv.halauncher.data.models.TokenResult
import xyz.mcmxciv.halauncher.domain.models.AuthenticationResult
import xyz.mcmxciv.halauncher.domain.models.Session
import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    val authenticationUrl: String
        get() = authenticationRepository.authenticationUrl

    suspend fun authenticate(authenticationCode: String) : AuthenticationResult =
        authenticationRepository.createSession(authenticationCode)

    fun getAuthenticationCode(url: String): String? =
        Uri.parse(url).getQueryParameter(AuthenticationRepository.RESPONSE_TYPE)

    fun verifyUrl(url: String): Boolean =
        url.contains(AuthenticationRepository.REDIRECT_URI)
}