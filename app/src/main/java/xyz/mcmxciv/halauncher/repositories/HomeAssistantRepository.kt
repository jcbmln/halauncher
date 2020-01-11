package xyz.mcmxciv.halauncher.repositories

import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.api.HomeAssistantApi
import xyz.mcmxciv.halauncher.api.HomeAssistantSecureApi
import xyz.mcmxciv.halauncher.models.DeviceIntegration
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import xyz.mcmxciv.halauncher.models.Session
import xyz.mcmxciv.halauncher.utils.AuthorizationException
import javax.inject.Inject

class HomeAssistantRepository @Inject constructor(
    private val homeAssistantApi: HomeAssistantApi,
    private val homeAssistantSecureApi: HomeAssistantSecureApi
) {
    suspend fun getToken(code: String): Session =
        homeAssistantApi.getToken(GRANT_TYPE_CODE, code, CLIENT_ID)

    private suspend fun refreshToken(refreshToken: String): Session {
        return homeAssistantApi.refreshToken(GRANT_TYPE_REFRESH, refreshToken, CLIENT_ID)
    }

    suspend fun revokeToken(session: Session?) {
        session?.let { homeAssistantApi.revokeToken(it.refreshToken!!, REVOKE_ACTION) }
    }

    suspend fun validateToken(cachedSession: Session?): Session {
        val token = cachedSession ?: throw AuthorizationException()
        return if (token.isExpired()) {
            val refreshToken = refreshToken(token.refreshToken!!)
            Session(
                refreshToken.accessToken,
                refreshToken.expiresIn,
                token.refreshToken,
                refreshToken.tokenType
            )
        }
        else token
    }

    suspend fun registerDevice(device: DeviceRegistration): DeviceIntegration {
        return homeAssistantSecureApi.registerDevice(device)
    }

    fun getAuthenticationUrl(baseUrl: String): String {
        return baseUrl.toHttpUrl()
            .newBuilder()
            .addPathSegments("auth/authorize")
            .addEncodedQueryParameter("response_type", RESPONSE_TYPE)
            .addEncodedQueryParameter("client_id", CLIENT_ID)
            .addEncodedQueryParameter("redirect_uri", REDIRECT_URI)
            .build()
            .toString()
    }

    companion object {
        const val CLIENT_ID = "https://halauncher.app"
        const val RESPONSE_TYPE = "code"
        const val GRANT_TYPE_CODE = "authorization_code"
        const val GRANT_TYPE_REFRESH = "refresh_token"
        const val REVOKE_ACTION = "revoke"
        const val REDIRECT_URI = "hass://auth"
    }
}