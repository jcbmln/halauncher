package xyz.mcmxciv.halauncher.data.interactors

import org.json.JSONObject
import org.threeten.bp.Instant
import xyz.mcmxciv.halauncher.data.authentication.AuthenticationException
import xyz.mcmxciv.halauncher.data.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.domain.models.Session
import javax.inject.Inject

class SessionInteractor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val localStorageRepository: LocalStorageRepository
) {
    val isDeviceRegistered: Boolean
        get() = localStorageRepository.webhookInfo != null

    val isAuthenticated: Boolean
        get() = localStorageRepository.session != null

    suspend fun createSession(authenticationCode: String) {
        val token = authenticationRepository.getToken(authenticationCode)
        localStorageRepository.session =
            Session(
                token.accessToken,
                Instant.now().epochSecond + token.expiresIn,
                token.refreshToken!!,
                token.tokenType
            )
    }

    suspend fun revokeSession() {
        val token = localStorageRepository.session?.accessToken ?: ""
        authenticationRepository.revokeToken(token)
        localStorageRepository.session = null
    }

    suspend fun getExternalAuthentication(): String {
        var session = localStorageRepository.session ?: throw AuthenticationException()

        if (session.isExpired) {
            val token = authenticationRepository.refreshToken(session.refreshToken)
            session = Session(
                token.accessToken,
                Instant.now().epochSecond + token.expiresIn,
                session.refreshToken,
                token.tokenType
            )
            localStorageRepository.session = session
        }

        return JSONObject(
            mapOf(
                "access_token" to session.accessToken,
                "expires_in" to session.expiresIn
            )
        ).toString()
    }
}