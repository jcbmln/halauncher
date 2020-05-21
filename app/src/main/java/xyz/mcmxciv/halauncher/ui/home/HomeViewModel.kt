package xyz.mcmxciv.halauncher.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.LocalCache
import xyz.mcmxciv.halauncher.data.interactors.AppsInteractor
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.data.interactors.UrlInteractor
import xyz.mcmxciv.halauncher.models.ErrorState
import xyz.mcmxciv.halauncher.models.WebCallback
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.ui.main.shortcuts.ShortcutPopupWindow
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val urlInteractor: UrlInteractor,
    private val sessionInteractor: SessionInteractor,
    private val resourceProvider: ResourceProvider,
    private val appsInteractor: AppsInteractor,
    private val localCache: LocalCache
) : ViewModel(), ShortcutPopupWindow.ShortcutActionListener {
    val webviewUrl: String
        get() = urlInteractor.externalAuthUrl

    private val callbackEvent = LiveEvent<WebCallback>()
    val callback: LiveData<WebCallback> = callbackEvent

    private val errorEvent = LiveEvent<ErrorState>()
    val error: LiveData<ErrorState> = errorEvent

    private val themeData = MutableLiveData<HassTheme>()
    val theme: LiveData<HassTheme> = themeData

    private val appListItemData = MutableLiveData<List<AppListItem>>().also { data ->
        viewModelScope.launch {
            data.postValue(appsInteractor.getAppListItems())
        }
    }
    val appListItems: LiveData<List<AppListItem>> = appListItemData

    init {
        val theme = localCache.theme ?: HassTheme.createDefaultTheme(resourceProvider)
        themeData.postValue(theme)
        localCache.theme = theme
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

    fun setTheme(json: String) {
        val theme = HassTheme.createFromString(json, resourceProvider)
        themeData.postValue(theme)
        localCache.theme = theme
    }

    override fun onHideActivity(activityName: String) {
        TODO("Not yet implemented")
    }
}
