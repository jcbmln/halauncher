package xyz.mcmxciv.halauncher.data.interactors

import org.json.JSONObject
import org.threeten.bp.Instant
import xyz.mcmxciv.halauncher.data.AuthenticationException
import xyz.mcmxciv.halauncher.data.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.models.Session
import javax.inject.Inject

class SessionInteractor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val localStorageRepository: LocalStorageRepository
) {
    val isDeviceRegistered: Boolean
        get() = localStorageRepository.deviceIntegration != null

    val isAuthenticated: Boolean
        get() = localStorageRepository.session != null

    suspend fun createSession(authenticationCode: String) {
        val token = authenticationRepository.getToken(authenticationCode)
        localStorageRepository.session = Session(
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
        localStorageRepository.baseUrl = LocalStorageRepository.PLACEHOLDER_URL
    }

    fun getExternalAuthentication(): String {
        val session = localStorageRepository.session ?: throw AuthenticationException()
        return JSONObject(
            mapOf(
                "access_token" to session.accessToken,
                "expires_in" to session.expiresIn
            )
        ).toString()
    }
}