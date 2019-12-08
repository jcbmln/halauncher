package xyz.mcmxciv.halauncher.fragments.home

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.repositories.ApplicationRepository
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.utils.AppPreferences
import xyz.mcmxciv.halauncher.utils.BaseViewModel
import javax.inject.Inject

class HomeViewModel : BaseViewModel() {
    @Inject
    lateinit var authenticationRepository: AuthenticationRepository

    private val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())

    private val appListExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.message.toString())
    }

    val appList: MutableLiveData<List<AppInfo>> by lazy {
        MutableLiveData<List<AppInfo>>().also {
            viewModelScope.launch(appListExceptionHandler) {
                val apps = ApplicationRepository().getAppList()
                appList.value = apps
            }
        }
    }

    val externalAuthCallback: MutableLiveData<Pair<String, String>> = MutableLiveData()
    val externalAuthRevokeCallback: MutableLiveData<String> = MutableLiveData()
    val sessionValidated: MutableLiveData<Boolean> = MutableLiveData()

    fun validateSession() {
        viewModelScope.launch {
            sessionValidated.value = if (prefs.token != null) {
                prefs.token = authenticationRepository.validateToken(prefs.token!!)
                true
            }
            else false
        }
    }

    fun getExternalAuth(callback: String) {
        viewModelScope.launch {
            prefs.token = authenticationRepository.validateToken(prefs.token!!)
            externalAuthCallback.value = Pair(
                callback,
                JSONObject(mapOf(
                    "access_token" to prefs.token?.accessToken,
                    "expires_in" to prefs.token?.expiresIn
                )).toString()
            )
        }
    }

    fun revokeExternalAuth(callback: String) {
        viewModelScope.launch {
            authenticationRepository.revokeToken(prefs.token?.refreshToken!!)
            prefs.token = null
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