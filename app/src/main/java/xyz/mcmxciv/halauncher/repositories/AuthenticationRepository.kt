package xyz.mcmxciv.halauncher.repositories

import dagger.Reusable
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.utils.AppPreferences
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.dao.SessionDao
import xyz.mcmxciv.halauncher.models.Token
import xyz.mcmxciv.halauncher.api.AuthenticationApi
import xyz.mcmxciv.halauncher.models.Session
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class AuthenticationRepository @Inject constructor(
    private val api: AuthenticationApi,
    private val sessionDao: SessionDao
) {
    suspend fun getToken(code: String): Token =
        api.getToken(GRANT_TYPE_CODE, code, CLIENT_ID)

    private suspend fun refreshToken(refreshToken: String): Token =
        api.refreshToken(GRANT_TYPE_REFRESH, refreshToken, CLIENT_ID)

    suspend fun revokeToken(refreshToken: String) = api.revokeToken(refreshToken, REVOKE_ACTION)

    suspend fun validateToken(token: Token): Token {
        return if (token.isExpired())
            refreshToken(token.refreshToken!!)
        else token
    }

//    suspend fun saveSession(token: Token) {
//        sessionDao.insertSession(Session(
//            token.refreshToken!!,
//            token.accessToken,
//            token.expiresIn + Instant.now().epochSecond,
//            token.tokenType
//        ))
//    }

//    suspend fun updateSession(token: Token) {
//        sessionDao.updateSession(Session(
//            token.refreshToken!!,
//            token.accessToken,
//            token.expiresIn + Instant.now().epochSecond,
//            token.tokenType
//        ))
//    }

//    suspend fun validateSession(): Session? {
//        val session = sessionDao.getSession()
//
//        if (session != null && session.isExpired()) {
//            val token = refreshToken(session.refreshToken)
//            updateSession(token)
//        }
//
//        return session
//    }
//
    suspend fun bearerToken(token: Token): String {
        val accessToken = validateToken(token).accessToken
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