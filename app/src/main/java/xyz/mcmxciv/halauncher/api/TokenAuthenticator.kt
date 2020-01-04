package xyz.mcmxciv.halauncher.api

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
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

        val refreshToken = appSettings.token?.refreshToken ?: throw AuthorizationException()
        val token = homeAssistantApi.refreshTokenSync(
            HomeAssistantRepository.GRANT_TYPE_REFRESH, refreshToken,
            HomeAssistantRepository.CLIENT_ID
        )

        appSettings.token = token
        return response.request.newBuilder()
            .addHeader("Authorization", "Bearer ${token.accessToken}")
            .build()
    }
}