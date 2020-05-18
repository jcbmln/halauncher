package xyz.mcmxciv.halauncher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.interactors.IntegrationInteractor
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.data.interactors.UrlInteractor
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val urlInteractor: UrlInteractor,
    private val sessionInteractor: SessionInteractor,
    private val integrationInteractor: IntegrationInteractor
) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        Timber.e(ex.message.toString())
    }

    var homeAssistantUrl: String
        get() = urlInteractor.baseUrl
        set(value) { urlInteractor.baseUrl = value }

    var deviceName: String
        get() = integrationInteractor.deviceInfo.deviceName!!
        set(value) {
            viewModelScope.launch {
                integrationInteractor.updateRegistration(value)
            }
        }

    var sensorUpdateInterval: Long
        get() = integrationInteractor.sensorUpdateInterval
        set(value) { integrationInteractor.sensorUpdateInterval = value }


    fun revokeToken() {
        viewModelScope.launch(exceptionHandler) {
            sessionInteractor.revokeSession()
        }
    }
}
