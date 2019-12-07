package xyz.mcmxciv.halauncher.fragments

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.utils.BaseViewModel
import javax.inject.Inject

class AuthenticationViewModel : BaseViewModel() {
    @Inject
    lateinit var authenticationRepository: AuthenticationRepository

    val authenticationErrorMessage: MutableLiveData<String> = MutableLiveData()
    val authenticationSuccess: MutableLiveData<Boolean> = MutableLiveData()

    private val authenticationExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.message.toString())
        authenticationErrorMessage.value = "Authentication failed."
        authenticationSuccess.value = false
    }

    fun authenticate(url: String): Boolean {
        val code = Uri.parse(url).getQueryParameter(AuthenticationRepository.RESPONSE_TYPE)
        return if (url.contains(AuthenticationRepository.REDIRECT_URI) && !code.isNullOrBlank()) {
            viewModelScope.launch(authenticationExceptionHandler) {
                val token = authenticationRepository.getToken(code)
                authenticationRepository.saveSession(token)

                //AuthenticationRepository().setSession(code)
                authenticationSuccess.value = true
            }

            true
        }
        else false
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }
}
