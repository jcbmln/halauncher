package xyz.mcmxciv.halauncher.repositories

import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.api.HomeAssistantApi
import xyz.mcmxciv.halauncher.models.DeviceIntegration
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import xyz.mcmxciv.halauncher.models.Token
import javax.inject.Inject

class HomeAssistantRepository @Inject constructor(
    private val api: HomeAssistantApi
) {
    suspend fun getToken(code: String): Token =
        api.getToken(GRANT_TYPE_CODE, code, CLIENT_ID)

    private suspend fun refreshToken(refreshToken: String): Token =
        api.refreshToken(GRANT_TYPE_REFRESH, refreshToken, CLIENT_ID)

    suspend fun revokeToken(token: Token?) {
        token?.let { api.revokeToken(it.refreshToken!!, REVOKE_ACTION) }
    }

    suspend fun validateToken(token: Token): Token {
        return if (token.isExpired()) {
            val refreshToken = refreshToken(token.refreshToken!!)
            Token(
                refreshToken.accessToken,
                refreshToken.expiresIn,
                token.refreshToken,
                refreshToken.tokenType
            )
        }
        else token
    }

    suspend fun bearerToken(token: Token): String {
        val accessToken = validateToken(token).accessToken
        return "Bearer $accessToken"
    }

    suspend fun registerDevice(bearerToken: String, device: DeviceRegistration): DeviceIntegration {
        return api.registerDevice(bearerToken, device)
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