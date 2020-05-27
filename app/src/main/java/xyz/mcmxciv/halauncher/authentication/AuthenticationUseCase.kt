package xyz.mcmxciv.halauncher.authentication

import android.net.Uri
import org.json.JSONObject
import xyz.mcmxciv.halauncher.domain.models.Session
import xyz.mcmxciv.halauncher.domain.models.TokenResult
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import java.time.Instant
import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository
) {
    val authenticationUrl: String
        get() = authenticationRepository.getAuthenticationUrl(settingsRepository.instanceUrl)

    suspend fun authenticate(authenticationCode: String): AuthenticationResult {
        return when (val result = authenticationRepository.getToken(authenticationCode)) {
            is TokenResult.Success -> {
                authenticationRepository.session = Session(result.token)
                AuthenticationResult.SUCCESS
            }
            is TokenResult.InvalidRequest -> AuthenticationResult.INVALID_REQUEST
            is TokenResult.InactiveUser -> AuthenticationResult.INACTIVE_USER
            is TokenResult.UnknownError -> AuthenticationResult.UNKNOWN_ERROR
        }
    }

    fun verifyAuthentication(): Boolean =
        authenticationRepository.session != null

    suspend fun revokeAuthentication() {
        authenticationRepository.session?.also { session ->
            val token = session.accessToken
            authenticationRepository.revokeToken(token)
            authenticationRepository.session = null
        }
    }

    suspend fun getExternalAuthentication(): String {
        var session = authenticationRepository.session ?: throw AuthenticationException()

        if (session.isExpired) {
            val token = authenticationRepository.refreshToken(session.refreshToken)
            session = Session(
                token.accessToken,
                Instant.now().epochSecond + token.expiresIn,
                session.refreshToken,
                token.tokenType
            )
            authenticationRepository.session = session
        }

        return JSONObject(
            mapOf(
                "access_token" to session.accessToken,
                "expires_in" to session.expiresIn
            )
        ).toString()
    }

    fun getAuthenticationCode(url: String): String? =
        Uri.parse(url).getQueryParameter(AuthenticationRepository.RESPONSE_TYPE)

    fun verifyUrl(url: String): Boolean =
        url.contains(AuthenticationRepository.REDIRECT_URI)
}
