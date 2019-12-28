package xyz.mcmxciv.halauncher.authentication

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor(
    private val homeAssistantRepository: HomeAssistantRepository,
    private val appSettings: AppSettings
) : ViewModel() {
    val authenticationErrorMessage: MutableLiveData<String> = MutableLiveData()
    val authenticationSuccess: MutableLiveData<Boolean> = MutableLiveData()

    private val authenticationExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.message.toString())
        authenticationErrorMessage.value = "Authentication failed."
        authenticationSuccess.value = false
    }

    fun authenticate(url: String): Boolean {
        val code = Uri.parse(url).getQueryParameter(HomeAssistantRepository.RESPONSE_TYPE)
        return if (url.contains(HomeAssistantRepository.REDIRECT_URI) && !code.isNullOrBlank()) {
            viewModelScope.launch(authenticationExceptionHandler) {
                appSettings.token = homeAssistantRepository.getToken(code)
                authenticationSuccess.value = true
            }

            true
        }
        else false
    }

    fun getAuthenticationUrl(): String {
        return homeAssistantRepository.getAuthenticationUrl(appSettings.url)
    }

    fun isSetupDone(): Boolean {
        return appSettings.setupDone
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }
}
