package xyz.mcmxciv.halauncher.activities.authentication

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository

class AuthenticationViewModel : ViewModel() {
    fun shouldRedirect(url: String): Boolean {
        val code = Uri.parse(url).getQueryParameter(AuthenticationRepository.RESPONSE_TYPE)
        return if (url.contains(AuthenticationRepository.REDIRECT_URI) && !code.isNullOrBlank()) {
            viewModelScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    AuthenticationRepository().setAuthToken(code)
                }
            }

            true
        }
        else {
            false
        }
    }

    companion object {
        private const val TAG = "AuthenticationViewModel"
    }
}