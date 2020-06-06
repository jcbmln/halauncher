package xyz.mcmxciv.halauncher.authentication

import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.authentication.models.Session
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository
) {
    val authenticationUrl: String
        get() = settingsRepository.instanceUrl.toHttpUrl().newBuilder()
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

    suspend fun authenticate(authenticationCode: String) {
        val token = authenticationRepository.getToken(authenticationCode)
        authenticationRepository.saveSession(
            Session(
                token
            )
        )
    }

    fun getAuthenticationCode(responseUrl: String): String? =
        responseUrl.toHttpUrl().queryParameter(AuthenticationRepository.RESPONSE_TYPE)

    fun isValidResponseUrl(responseUrl: String): Boolean =
        responseUrl.contains(AuthenticationRepository.REDIRECT_URI)
}
