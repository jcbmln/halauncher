package xyz.mcmxciv.halauncher.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.data.cache.PreferencesLocalCache
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val localCache: PreferencesLocalCache
) : ViewModel() {
    private val _errorEvent = LiveEvent<Int>()
    val errorEvent: LiveData<Int> = _errorEvent

    private val _navigationEvent = LiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    val authenticationUrl: String
        get() = authenticationUseCase.authenticationUrl

    fun authenticate(url: String): Boolean {
        val code = authenticationUseCase.getAuthenticationCode(url)
        return if (authenticationUseCase.verifyUrl(url) && !code.isNullOrBlank()) {
            val exceptionHandler = CoroutineExceptionHandler { _, ex ->
                Timber.e(ex)
                _errorEvent.postValue(R.string.error_authentication_failed_message)
            }

            viewModelScope.launch(exceptionHandler) {
                authenticationUseCase.authenticate(code)

                val action = if (localCache.webhookInfo != null)
                    AuthenticationFragmentDirections.actionGlobalHomeFragment()
                else AuthenticationFragmentDirections
                    .actionAuthenticationFragmentToIntegrationFragment()

                _navigationEvent.postValue(action)
            }

            true
        } else false
    }

    fun webviewError() {
        _errorEvent.postValue(R.string.error_connection_failed_message)
    }
}
