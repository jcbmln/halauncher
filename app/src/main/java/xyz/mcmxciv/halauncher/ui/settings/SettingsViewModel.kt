package xyz.mcmxciv.halauncher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        Timber.e(ex.message.toString())
    }


    fun revokeToken() {
        viewModelScope.launch(exceptionHandler) {
//            homeAssistantRepository.revokeToken(launcherSettings.session)
//            launcherSettings.session = null
        }
    }
}
