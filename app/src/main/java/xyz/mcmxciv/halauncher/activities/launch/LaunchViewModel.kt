package xyz.mcmxciv.halauncher.activities.launch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository
import javax.inject.Inject

class LaunchViewModel : ViewModel() {
    @Inject
    lateinit var authenticationRepository: AuthenticationRepository

    val sessionValidated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun validateSession() {
        viewModelScope.launch {
            val session = authenticationRepository.validateSession()
            sessionValidated.value = session != null
        }
    }
}