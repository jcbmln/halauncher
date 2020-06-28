package xyz.mcmxciv.halauncher.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.HalauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.authentication.AuthenticationException
import xyz.mcmxciv.halauncher.authentication.AuthenticationUseCase
import xyz.mcmxciv.halauncher.device.DeviceProfile
import xyz.mcmxciv.halauncher.settings.SettingsUseCase
import xyz.mcmxciv.halauncher.utils.HassTheme
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import java.io.BufferedReader

class HomeViewModel @ViewModelInject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val resourceProvider: ResourceProvider,
    private val deviceProfile: DeviceProfile
) : BaseViewModel() {
    val appDrawerColumns: Int
        get() = deviceProfile.appDrawerColumns

    private val _authenticationUrl = MutableLiveData<String>().also {
        it.postValue(authenticationUseCase.authenticationUrl)
    }
    val authenticationUrl: LiveData<String> = _authenticationUrl

    private val _callbackEvent = LiveEvent<String>()
    val callbackEvent: LiveData<String> = _callbackEvent

    private val _theme = MutableLiveData<HassTheme>().also {
        it.postValue(settingsUseCase.theme)
    }
    val theme: LiveData<HassTheme> = _theme

    val themeCallback: String?
        get() {
            val callback = try {
                val input = HalauncherApplication.instance.assets.open("websocketBridge.js")
                input.bufferedReader().use(BufferedReader::readText)
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }

            return callback?.let { "javascript:(function() { $it })()" }
        }

    fun getExternalAuth(result: String) {
        val callback = JSONObject(result).getString("callback")
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)

            val errorMessage = if (ex is AuthenticationException) {
                R.string.authentication_failed
            } else R.string.error_loading_homeassistant
            errorEvent.postValue(errorMessage)
            _callbackEvent.postValue("$callback(false);")
        }

        viewModelScope.launch(exceptionHandler) {
            val externalAuthentication = authenticationUseCase.getExternalAuthentication()
            if (!externalAuthentication.isNullOrBlank()) {
                _callbackEvent.postValue("$callback(true, $externalAuthentication);")
            } else {
                errorEvent.postValue(R.string.authentication_failed)
                navigationEvent.postValue(
                    HomeFragmentDirections.actionHomeFragmentToAuthenticationFragment()
                )
            }
        }
    }

    fun revokeExternalAuth(result: String) {
        val callback = JSONObject(result).getString("callback")
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            _callbackEvent.postValue("$callback(false);")
        }

        viewModelScope.launch(exceptionHandler) {
            authenticationUseCase.revokeAuthentication()
            _callbackEvent.postValue("$callback(true);")
            navigationEvent.postValue(
                HomeFragmentDirections.actionHomeFragmentToAuthenticationFragment()
            )
        }
    }

    fun setTheme(result: String) {
        val theme = HassTheme.createFromString(result, resourceProvider)
        _theme.postValue(theme)
        settingsUseCase.theme = theme
    }

    fun parseMessage(message: String) {
        when (JSONObject(message).get("type")) {
            "config/get" -> {
                val script = "externalBus(${JSONObject(
                    mapOf(
                        "id" to JSONObject(message).get("id"),
                        "type" to "result",
                        "success" to true,
                        "result" to JSONObject(mapOf("hasSettingsScreen" to true))
                    )
                )});"
                _callbackEvent.postValue(script)
            }
            "config_screen/show" -> {}
        }
    }

    fun onCancelLoad() {
        navigationEvent.postValue(
            HomeFragmentDirections.actionHomeFragmentToAuthenticationFragment()
        )
    }

    fun showWebError() {
        errorEvent.postValue(R.string.error_loading_homeassistant)
    }

    fun showSslError() {
        errorEvent.postValue(R.string.ssl_error)
    }
}
