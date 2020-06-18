package xyz.mcmxciv.halauncher.authentication

import androidx.core.net.toUri
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import xyz.mcmxciv.halauncher.authentication.models.Session
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import xyz.mcmxciv.halauncher.utils.Resource
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

    suspend fun validateAuthentication(): Boolean {
        val session = authenticationRepository.session ?: return false

        return if (session.isExpired) {
            val resource = authenticationRepository.refreshToken(session.refreshToken)

            if (resource.status == Resource.Status.SUCCESS) return true
            else authenticationRepository.revokeToken(session.refreshToken)

            false
        } else true
    }

    suspend fun getExternalAuthentication(): String? {
        return if (validateAuthentication()) {
            val session = authenticationRepository.session ?: throw AuthenticationException()
            JSONObject(
                mapOf(
                    "access_token" to session.accessToken,
                    "expires_in" to session.expiresIn
                )
            ).toString()
        } else null
    }

    suspend fun revokeAuthentication() {
        authenticationRepository.session?.also { session ->
            val token = session.accessToken
            authenticationRepository.revokeToken(token)
            authenticationRepository.clearSession()
        }
    }

    fun getAuthenticationCode(responseUrl: String): String? =
        responseUrl.toUri().getQueryParameter(AuthenticationRepository.RESPONSE_TYPE)

    fun isValidResponseUrl(responseUrl: String): Boolean =
        responseUrl.contains(AuthenticationRepository.REDIRECT_URI)
}
