package xyz.mcmxciv.halauncher.activities.home

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import xyz.mcmxciv.halauncher.repositories.ApplicationRepository
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.models.Session
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository

class HomeViewModel : ViewModel() {
    val appList: MutableLiveData<List<AppInfo>> by lazy {
        MutableLiveData<List<AppInfo>>().also {
            viewModelScope.launch(appListExceptionHandler) {
                val apps = ApplicationRepository().getAppList()
                appList.value = apps
            }
        }
    }

    val externalAuthCallback: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    val externalAuthRevokeCallback: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val appListExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.message.toString())
    }

    fun getExternalAuth(callback: String) {
        viewModelScope.launch {
            val session = AuthenticationRepository().validateSession()
            externalAuthCallback.value = Pair(
                callback,
                JSONObject(mapOf(
                    "access_token" to session.accessToken,
                    "expires_in" to session.expiresIn
                )).toString()
            )
        }
    }

    fun revokeExternalAuth(callback: String) {
        viewModelScope.launch {
            AuthenticationRepository().clearSession()
            externalAuthRevokeCallback.value = callback
        }
    }

    fun buildUrl(baseUrl: String): String {
        return baseUrl.toUri()
            .buildUpon()
            .appendQueryParameter("external_auth", "1")
            .build()
            .toString()
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}