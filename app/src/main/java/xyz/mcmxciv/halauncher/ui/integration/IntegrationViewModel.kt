package xyz.mcmxciv.halauncher.ui.integration

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import xyz.mcmxciv.halauncher.repositories.HomeAssistantRepository
import xyz.mcmxciv.halauncher.utils.AppSettings
import xyz.mcmxciv.halauncher.utils.ResourceLiveData
import javax.inject.Inject

class IntegrationViewModel @Inject constructor(
    private val homeAssistantRepository: HomeAssistantRepository,
    private val appSettings: AppSettings
) : ViewModel() {
    val integrationState = ResourceLiveData<IntegrationState>()
    val integrationError: MutableLiveData<String> = MutableLiveData()

//    private val integrationExceptionHandler = CoroutineExceptionHandler { _, exception ->
//        Log.e(TAG, exception.message.toString())
//        integrationState.value = IntegrationState.FAILED
//        integrationError.value = "Unable to register device."
//    }

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

        integrationState.postValue(viewModelScope, "Unable to register device.") {
            homeAssistantRepository.registerDevice(deviceRegistration)
            return@postValue IntegrationState.SUCCESS
        }
    }

    fun finishSetup() {
        appSettings.setupDone = true
    }

    enum class IntegrationState {
        SUCCESS
    }

//    companion object {
//        private const val TAG = "IntegrationViewModel"
//    }
}
