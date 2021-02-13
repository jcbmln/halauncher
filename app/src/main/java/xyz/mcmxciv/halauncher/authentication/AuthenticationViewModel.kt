package xyz.mcmxciv.halauncher.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val integrationUseCase: IntegrationUseCase
) : BaseViewModel() {
    private val _authenticationUrl = MutableLiveData<String>().also {
        it.postValue(authenticationUseCase.authenticationUrl)
    }
    val authenticationUrl: LiveData<String> = _authenticationUrl

    fun authenticate(responseUrl: String): Boolean {
        if (authenticationUseCase.isValidResponseUrl(responseUrl)) {
            val code = authenticationUseCase.getAuthenticationCode(responseUrl)

            if (!code.isNullOrEmpty()) {
                val exceptionHandler = CoroutineExceptionHandler { _, ex ->
                    Timber.e(ex)
                    errorEvent.postValue(R.string.error_on_authentication)
                }

                viewModelScope.launch(exceptionHandler) {
                    authenticationUseCase.authenticate(code)

                    val action = if (integrationUseCase.isDeviceIntegrated)
                        AuthenticationFragmentDirections
                            .actionAuthenticationFragmentToHomeFragment()
                    else AuthenticationFragmentDirections
                        .actionAuthenticationFragmentToIntegrationFragment()

                    navigationEvent.postValue(action)
                }
            } else return false

            return true
        } else return false
    }

    fun showWebError() {
        errorEvent.postValue(R.string.error_loading_homeassistant)
    }

    fun showSslError() {
        errorEvent.postValue(R.string.ssl_error)
    }
}
