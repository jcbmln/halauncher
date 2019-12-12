package xyz.mcmxciv.halauncher.fragments.home

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.models.Token
import xyz.mcmxciv.halauncher.repositories.ApplicationRepository
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import xyz.mcmxciv.halauncher.utils.AppSettings
import xyz.mcmxciv.halauncher.utils.AuthorizationException
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val homeAssistantRepository: HomeAssistantRepository,
    private val applicationRepository: ApplicationRepository,
    private val appSettings: AppSettings
) : ViewModel() {
    private val appListExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.message.toString())
    }

    val appList: MutableLiveData<List<AppInfo>> by lazy {
        MutableLiveData<List<AppInfo>>().also {
            viewModelScope.launch(appListExceptionHandler) {
                val apps = applicationRepository.getAppList()
                appList.value = apps
            }
        }
    }

    val externalAuthCallback = MutableLiveData<Pair<String, String>>()
    val externalAuthRevokeCallback = MutableLiveData<String>()
    val sessionValidated = MutableLiveData<Boolean>()

    fun validateSession() {
        val token = appSettings.token!!
        viewModelScope.launch {
            sessionValidated.value = if (token.accessToken.isNotEmpty()) {
                appSettings.token = homeAssistantRepository.validateToken(token)
                true
            }
            else false
        }
    }

    fun getExternalAuth(callback: String) {
        var token: Token = appSettings.token ?: throw AuthorizationException()
        viewModelScope.launch {
            token = homeAssistantRepository.validateToken(token).also { appSettings.token = it }
            externalAuthCallback.value = Pair(
                callback,
                JSONObject(mapOf(
                    "access_token" to token.accessToken,
                    "expires_in" to token.expiresIn
                )).toString()
            )
            appSettings.token = token
        }
    }

    fun revokeExternalAuth(callback: String) {
        viewModelScope.launch {
            homeAssistantRepository.revokeToken(appSettings.token)
            appSettings.token = null
            externalAuthRevokeCallback.value = callback
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

    fun isSetupDone(): Boolean = appSettings.setupDone

    companion object {
        private const val TAG = "HomeViewModel"
    }
}