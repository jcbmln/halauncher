package xyz.mcmxciv.halauncher.activities.launch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository

class LaunchViewModel : ViewModel() {
    val sessionValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun validateSession() {
        viewModelScope.launch {
            val session = AuthenticationRepository().validateSession()
            sessionValidated.value = session != null
        }
    }
}