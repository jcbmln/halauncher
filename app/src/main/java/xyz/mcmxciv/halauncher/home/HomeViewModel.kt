package xyz.mcmxciv.halauncher.home

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.json.JSONObject
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.repositories.ApplicationRepository
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import xyz.mcmxciv.halauncher.utils.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val homeAssistantRepository: HomeAssistantRepository,
    private val applicationRepository: ApplicationRepository,
    private val appSettings: AppSettings
) : ViewModel() {
    val webCallback = ResourceLiveData<WebCallback>()

    val sessionState by lazy {
        val liveData = ResourceLiveData<SessionState>()
        val cachedSession = appSettings.session

        if (!appSettings.setupDone) {
            liveData.postSuccess(SessionState.NewUser)
        }
        else if (cachedSession == null || cachedSession.accessToken.isEmpty()) {
            liveData.postSuccess(SessionState.Invalid)
        }
        else {
            liveData.postValue(viewModelScope, "Failed to validate session.") {
                appSettings.session = homeAssistantRepository.validateSession(cachedSession)
                return@postValue SessionState.Valid
            }
        }

        return@lazy liveData
    }

    val launchableActivities by lazy {
        val liveData = ResourceLiveData<List<AppInfo>>()
        liveData.postValue(viewModelScope, "Failed to get app list.") {
            return@postValue applicationRepository.getAppList()
        }

        return@lazy liveData
    }

    fun getExternalAuth(callback: String) {
        val cachedToken = appSettings.session
        val errorCallback = Resource.Error(
            WebCallback.AuthCallback("$callback(false);"),
            "Failed to authenticate."
        )

        webCallback.postValue(viewModelScope, errorCallback) {
            val token = homeAssistantRepository.validateSession(cachedToken)
            appSettings.session = token

            val json = JSONObject(mapOf(
                "access_token" to token.accessToken,
                "expires_in" to token.expiresIn
            )).toString()

            return@postValue WebCallback.AuthCallback("$callback(true, $json);")
        }
    }

    fun revokeExternalAuth(callback: String) {
        val errorCallback = Resource.Error(
            WebCallback.RevokeAuthCallback("$callback(false);"),
            "Failed to revoke access."
        )

        webCallback.postValue(viewModelScope, errorCallback) {
            homeAssistantRepository.revokeToken(appSettings.session)
            appSettings.session = null

            sessionState.postSuccess(SessionState.Invalid)
            return@postValue WebCallback.RevokeAuthCallback("$callback(true);")
        }
    }

    fun buildUrl(): String {
        val baseUrl = appSettings.url

        return baseUrl.toUri()
            .buildUpon()
            .appendQueryParameter("external_auth", "1")
            .build()
            .toString()
    }
}