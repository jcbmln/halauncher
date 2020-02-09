package xyz.mcmxciv.halauncher.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val homeAssistantRepository: HomeAssistantRepository,
    val appSettings: AppSettings
) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        Timber.e(ex.message.toString())
    }


    fun revokeToken() {
        viewModelScope.launch(exceptionHandler) {
            homeAssistantRepository.revokeToken(appSettings.session)
            appSettings.session = null
        }
    }
}
