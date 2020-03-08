package xyz.mcmxciv.halauncher.ui.home

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.interactors.AppsInteractor
import xyz.mcmxciv.halauncher.data.interactors.IntegrationInteractor
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.data.interactors.UrlInteractor
import xyz.mcmxciv.halauncher.data.models.Config
import xyz.mcmxciv.halauncher.models.ErrorState
import xyz.mcmxciv.halauncher.models.WebCallback
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val urlInteractor: UrlInteractor,
    private val sessionInteractor: SessionInteractor,
    private val appsInteractor: AppsInteractor,
    private val integrationInteractor: IntegrationInteractor
) : ViewModel() {
    val webviewUrl: String
        get() = urlInteractor.externalAuthUrl

    private val callbackEvent = LiveEvent<WebCallback>()
    val callback: LiveData<WebCallback> = callbackEvent

    private val errorEvent = LiveEvent<ErrorState>()
    val error: LiveData<ErrorState> = errorEvent

    val configEvent = LiveEvent<Config>().also { event ->
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
        }

        viewModelScope.launch(exceptionHandler) {
            event.postValue(integrationInteractor.getConfig())
        }
    }
    val config: LiveData<Config> = configEvent

    val appListItems = MutableLiveData<List<AppListItem>>().also {
        viewModelScope.launch {
            it.postValue(appsInteractor.getAppListItems())
        }
    }

    fun getExternalAuth(callback: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            val errorState = if (sessionInteractor.isAuthenticated) ErrorState.WEBVIEW
                             else ErrorState.AUTHENTICATION
            errorEvent.postValue(errorState)
            callbackEvent.postValue(WebCallback.AuthCallback("$callback(false);"))
        }

        viewModelScope.launch(exceptionHandler) {
            val externalAuthentication = sessionInteractor.getExternalAuthentication()
            val authCallback = WebCallback.AuthCallback(
                "$callback(true, $externalAuthentication);"
            )
            callbackEvent.postValue(authCallback)
        }
    }

    fun revokeExternalAuth(callback: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            callbackEvent.postValue(WebCallback.RevokeAuthCallback("$callback(false);"))
        }

        viewModelScope.launch(exceptionHandler) {
            sessionInteractor.revokeSession()
            callbackEvent.postValue(WebCallback.RevokeAuthCallback("$callback(true);"))
        }
    }
}