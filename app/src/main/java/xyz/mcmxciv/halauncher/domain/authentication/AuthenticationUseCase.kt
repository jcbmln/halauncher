package xyz.mcmxciv.halauncher.domain.authentication

import android.net.Uri
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.LocalStorage
import xyz.mcmxciv.halauncher.data.authentication.AuthenticationRepository
import xyz.mcmxciv.halauncher.data.models.TokenResult
import xyz.mcmxciv.halauncher.domain.models.AuthenticationResult
import xyz.mcmxciv.halauncher.domain.models.Session
import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val localStorage: LocalStorage
) {
    val authenticationUrl: String
        get() = localStorage.baseUrl.toHttpUrl()
            .newBuilder()
            .addPathSegments("auth/authorize")
            .addEncodedQueryParameter("response_type",
                AuthenticationRepository.RESPONSE_TYPE
            )
            .addEncodedQueryParameter("client_id",
                AuthenticationRepository.CLIENT_ID
            )
            .addEncodedQueryParameter("redirect_uri",
                AuthenticationRepository.REDIRECT_URI
            )
            .build()
            .toString()

    suspend fun authenticate(authenticationCode: String) : AuthenticationResult {
        return when (val result = authenticationRepository.getToken(authenticationCode)) {
            is TokenResult.Success -> {
                localStorage.session = Session(result.token)
                AuthenticationResult.Success
            }
            is TokenResult.InvalidRequest -> AuthenticationResult.InvalidRequest
            is TokenResult.InactiveUser -> AuthenticationResult.InactiveUser
            is TokenResult.Error -> AuthenticationResult.UnknownError
        }
    }

    fun getAuthenticationCode(url: String): String? =
        Uri.parse(url).getQueryParameter(AuthenticationRepository.RESPONSE_TYPE)

    fun verifyUrl(url: String): Boolean =
        url.contains(AuthenticationRepository.REDIRECT_URI)
}