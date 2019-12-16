package xyz.mcmxciv.halauncher.fragments.integration

import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Inject

class IntegrationViewModel @Inject constructor(
    private val homeAssistantRepository: HomeAssistantRepository,
    private val appSettings: AppSettings
) : ViewModel() {
    val integrationState: MutableLiveData<IntegrationState> = MutableLiveData()
    val integrationError: MutableLiveData<String> = MutableLiveData()

    private val integrationExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, exception.message.toString())
        integrationState.value = IntegrationState.FAILED
        integrationError.value = "Unable to register device."
    }

    fun registerDevice() {
        val deviceRegistration = DeviceRegistration(
            BuildConfig.APPLICATION_ID,
            "HALauncher",
            BuildConfig.VERSION_NAME,
            appSettings.deviceName,
            Build.MANUFACTURER,
            Build.MODEL,
            "Android",
            Build.VERSION.SDK_INT.toString(),
            false,
            null
        )

        viewModelScope.launch(integrationExceptionHandler) {
            val bearerToken = homeAssistantRepository.bearerToken(appSettings.token!!)
            homeAssistantRepository.registerDevice(bearerToken, deviceRegistration)
            integrationState.value = IntegrationState.SUCCESS
        }
    }

    fun finishSetup() {
        appSettings.setupDone = true
    }

    enum class IntegrationState {
        SUCCESS,
        FAILED
    }

    companion object {
        private const val TAG = "IntegrationViewModel"
    }
}
