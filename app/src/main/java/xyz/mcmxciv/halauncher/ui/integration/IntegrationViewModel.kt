package xyz.mcmxciv.halauncher.ui.integration

import android.os.Build
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.data.interactors.IntegrationInteractor
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.models.IntegrationState
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class IntegrationViewModel @Inject constructor(
    private val integrationInteractor: IntegrationInteractor,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val integrationEvent = LiveEvent<IntegrationState>()
    val integrationState: LiveData<IntegrationState> by lazy {
        registerDevice()
        return@lazy integrationEvent
    }

    fun registerDevice() {
        integrationEvent.postValue(IntegrationState.LOADING)

        val deviceRegistration =
            DeviceInfo(
                BuildConfig.APPLICATION_ID,
                resourceProvider.getString(R.string.app_name),
                BuildConfig.VERSION_NAME,
                resourceProvider.getSettingsString("bluetooth_name") ?: Build.MODEL,
                Build.MANUFACTURER,
                Build.MODEL,
                "Android",
                Build.VERSION.SDK_INT.toString(),
                false,
                null,
                resourceProvider.getSettingsString(Settings.Secure.ANDROID_ID)
            )

        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            integrationEvent.postValue(IntegrationState.ERROR)
        }

        viewModelScope.launch(exceptionHandler) {
            integrationInteractor.registerDevice(deviceRegistration)
            integrationEvent.postValue(IntegrationState.SUCCESS)
        }
    }
}
