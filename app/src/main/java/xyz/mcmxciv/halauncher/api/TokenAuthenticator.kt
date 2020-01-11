package xyz.mcmxciv.halauncher.api

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import xyz.mcmxciv.halauncher.models.Session
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import xyz.mcmxciv.halauncher.utils.AppSettings
import xyz.mcmxciv.halauncher.utils.AuthorizationException

class TokenAuthenticator constructor(
    private val appSettings: AppSettings,
    private val homeAssistantApiHolder: HomeAssistantApiHolder
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") != null)
            return null

        val homeAssistantApi = homeAssistantApiHolder.homeAssistantApi ?: return null
        val refreshToken = appSettings.session?.refreshToken ?: throw AuthorizationException()
        val refreshedToken = homeAssistantApi.refreshTokenSync(
            HomeAssistantRepository.GRANT_TYPE_REFRESH, refreshToken,
            HomeAssistantRepository.CLIENT_ID
        )
        val token = Session(
            refreshedToken.accessToken,
            refreshedToken.expiresIn,
            appSettings.session?.refreshToken,
            refreshedToken.tokenType
        )

        appSettings.session = token
        return response.request.newBuilder()
            .addHeader("Authorization", "Bearer ${token.accessToken}")
            .build()
    }
}