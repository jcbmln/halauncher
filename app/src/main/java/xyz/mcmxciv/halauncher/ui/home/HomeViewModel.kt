package xyz.mcmxciv.halauncher.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.domain.apps.AppsUseCase
import xyz.mcmxciv.halauncher.domain.settings.SettingsUseCase
import xyz.mcmxciv.halauncher.models.DeviceProfile
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val sessionInteractor: SessionInteractor,
    private val resourceProvider: ResourceProvider,
    private val appsUseCase: AppsUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val deviceProfile: DeviceProfile
) : ViewModel() {
    val webviewUrl: String
        get() = settingsUseCase.webviewUrl

    val appDrawerColumns: Int
        get() = deviceProfile.appDrawerColumns

    private val _callbackEvent = LiveEvent<String>()
    val callbackEvent: LiveData<String> = _callbackEvent

    private val _errorEvent = LiveEvent<String>()
    val errorEvent: LiveData<String> = _errorEvent

    private val _navigationEvent = LiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    private val _theme = MutableLiveData<HassTheme>()
    val theme: LiveData<HassTheme> = _theme

    private val appListItemData = MutableLiveData<List<AppListItem>>().also { data ->
        viewModelScope.launch {
            data.postValue(appsUseCase.getAppListItems())
        }
    }
    val appListItems: LiveData<List<AppListItem>> = appListItemData

    init {
        val theme = settingsUseCase.theme ?: HassTheme.createDefaultTheme(resourceProvider)
        _theme.postValue(theme)
        settingsUseCase.theme = theme
    }

    fun getExternalAuth(callback: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            val errorState = if (sessionInteractor.isAuthenticated) {
                _navigationEvent.postValue(
                    HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph()
                )
                resourceProvider.getString(R.string.error_no_session_message)
            } else {
                resourceProvider.getString(R.string.error_webview_message)
            }
            _errorEvent.postValue(errorState)
            _callbackEvent.postValue("$callback(false);")
        }

        viewModelScope.launch(exceptionHandler) {
            val externalAuthentication = sessionInteractor.getExternalAuthentication()
            _callbackEvent.postValue("$callback(true, $externalAuthentication);")
        }
    }

    fun revokeExternalAuth(callback: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            _callbackEvent.postValue("$callback(false);")
        }

        viewModelScope.launch(exceptionHandler) {
            sessionInteractor.revokeSession()
            _callbackEvent.postValue("$callback(true);")
            _navigationEvent.postValue(
                HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph()
            )
        }
    }

    fun setTheme(json: String) {
        val theme = HassTheme.createFromString(json, resourceProvider)
        _theme.postValue(theme)
        settingsUseCase.theme = theme
    }

    fun hideApp(activityName: String) {
    }
}
