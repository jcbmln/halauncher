package xyz.mcmxciv.halauncher.repositories

import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.utils.AppPreferences
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.services.AuthenticationService
import xyz.mcmxciv.halauncher.models.Session
import xyz.mcmxciv.halauncher.services.ServiceFactory
import java.lang.Exception

class AuthenticationRepository {
    private val service = ServiceFactory.createService(prefs.url, AuthenticationService::class.java)

    suspend fun setSession(code: String) {
        service.getToken(GRANT_TYPE_CODE, code, CLIENT_ID).let {
            Session.create(it)
        }
    }

    suspend fun clearSession() {
        val session = Session.get() ?: throw Exception()
        service.revokeToken(session.refreshToken, REVOKE_ACTION)
    }

    suspend fun validateSession(): Session? {
        val session = Session.get()

        if (session != null && session.isExpired) {
            return service.refreshToken(
                GRANT_TYPE_REFRESH,
                session.refreshToken,
                CLIENT_ID
            ).let {
                Session.create(it)
            }
        }

        return session
    }

    suspend fun bearerToken(): String {
        val accessToken = validateSession()?.accessToken
        return "Bearer $accessToken"
    }

    companion object {
        const val CLIENT_ID = "https://halauncher.app"
        const val RESPONSE_TYPE = "code"
        const val GRANT_TYPE_CODE = "authorization_code"
        const val GRANT_TYPE_REFRESH = "refresh_token"
        const val REVOKE_ACTION = "revoke"
        const val REDIRECT_URI = "hass://auth"

        private val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())

        val authenticationUrl: String
            get() {
                return prefs.url.toHttpUrl()
                    .newBuilder()
                    .addPathSegments("auth/authorize")
                    .addEncodedQueryParameter("response_type", RESPONSE_TYPE)
                    .addEncodedQueryParameter("client_id", CLIENT_ID)
                    .addEncodedQueryParameter("redirect_uri", REDIRECT_URI)
                    .build()
                    .toString()
            }
    }
}