package xyz.mcmxciv.halauncher.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.LocalStorage
import xyz.mcmxciv.halauncher.domain.authentication.AuthenticationUseCase
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val localStorage: LocalStorage
) : ViewModel() {
    private val authenticationEvent = LiveEvent<AuthenticationState>().also { event ->
        event.postValue(AuthenticationState.LOADING)
    }
    val authenticationState: LiveData<AuthenticationState> = authenticationEvent

    val authenticationUrl: String
        get() = authenticationUseCase.authenticationUrl

    val isSetupDone: Boolean
        get() = localStorage.deviceIntegration != null

    fun authenticate(url: String): Boolean {
        val code = authenticationUseCase.getAuthenticationCode(url)
        return if (authenticationUseCase.verifyUrl(url) && !code.isNullOrBlank()) {
            val exceptionHandler = CoroutineExceptionHandler { _, ex ->
                Timber.e(ex)
                authenticationEvent.postValue(AuthenticationState.ERROR)
            }

            viewModelScope.launch(exceptionHandler) {
                authenticationUseCase.authenticate(code)
                authenticationEvent.postValue(AuthenticationState.AUTHENTICATED)
            }

            true
        }
        else false
    }
}
