package xyz.mcmxciv.halauncher.ui.authentication

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.data.interactors.UrlInteractor
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.data.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor(
    private val sessionInteractor: SessionInteractor,
    private val urlInteractor: UrlInteractor
) : ViewModel() {
    private val authenticationEvent by lazy {
        val liveEvent = LiveEvent<AuthenticationState>()
        liveEvent.postValue(AuthenticationState.LOADING)
        return@lazy liveEvent
    }
    val authenticationState: LiveData<AuthenticationState> = authenticationEvent

    val authenticationUrl: String
        get() = urlInteractor.authenticationUrl

    val isSetupDone: Boolean
        get() = sessionInteractor.isDeviceRegistered

    fun authenticate(url: String): Boolean {
        val code = Uri.parse(url).getQueryParameter(HomeAssistantRepository.RESPONSE_TYPE)
        return if (url.contains(HomeAssistantRepository.REDIRECT_URI) && !code.isNullOrBlank()) {
            val exceptionHandler = CoroutineExceptionHandler { _, ex ->
                Timber.e(ex)
                authenticationEvent.postValue(AuthenticationState.ERROR)
            }

            viewModelScope.launch(exceptionHandler) {
                sessionInteractor.createSession(code)
                authenticationEvent.postValue(AuthenticationState.AUTHENTICATED)
            }

            true
        }
        else false
    }
}
