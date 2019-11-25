package xyz.mcmxciv.halauncher.repositories

import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.utils.AppPreferences
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.AuthenticationApi
import xyz.mcmxciv.halauncher.models.Session
import xyz.mcmxciv.halauncher.utils.ApiFactory
import java.time.Instant

class AuthenticationRepository {
    private val api: AuthenticationApi = ApiFactory.getAuthenticationApi(prefs.url)

    suspend fun setAuthToken(code: String) {
        api.getToken(GRANT_TYPE_CODE, code, CLIENT_ID).let {
            Session(
                it.accessToken,
                Instant.now().epochSecond + it.expiresIn,
                it.refreshToken!!,
                it.tokenType
            ).save()
        }
    }



    companion object {
        const val CLIENT_ID = "https://jcbmln.github.io/halauncher"
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