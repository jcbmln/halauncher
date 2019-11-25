package xyz.mcmxciv.halauncher.repositories

import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.utils.AppPreferences
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.AuthenticationApi
import xyz.mcmxciv.halauncher.utils.ApiFactory

class AuthenticationRepository {
    private val api: AuthenticationApi = ApiFactory.getAuthenticationApi(prefs.url)

    suspend fun setAuthToken(code: String) {
        api.getToken(GRANT_TYPE_CODE, code, CLIENT_ID).let {
            prefs.accessToken = it.accessToken
            prefs.expiresIn = it.expiresIn
            prefs.refreshToken = it.refreshToken
            prefs.tokenType = it.tokenType
        }
    }

    companion object {
        const val CLIENT_ID = "https://jcbmln.github.io/HALauncher"
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