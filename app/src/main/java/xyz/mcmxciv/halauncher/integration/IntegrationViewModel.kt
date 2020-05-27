package xyz.mcmxciv.halauncher.integration

import android.os.Build
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class IntegrationViewModel @Inject constructor(
    private val integrationUseCase: IntegrationUseCase,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val _navigationEvent = LiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    private val _errorEvent = LiveEvent<Int>()
    val errorEvent: LiveData<Int> = _errorEvent

    private val _buttonVisibility = MutableLiveData<Boolean>().also {
        it.postValue(false)
    }
    val buttonVisibility: LiveData<Boolean> = _buttonVisibility

    private val _progressVisibility = MutableLiveData<Boolean>().also {
        it.postValue(true)
    }
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    init {
        registerDevice()
    }

    fun registerDevice() {
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
            _errorEvent.postValue(R.string.integration_status_failed)
            _buttonVisibility.postValue(true)
            _progressVisibility.postValue(false)
        }

        viewModelScope.launch(exceptionHandler) {
            integrationUseCase.registerDevice(deviceRegistration)
            _navigationEvent.postValue(IntegrationFragmentDirections.actionGlobalHomeFragment())
        }
    }

    fun skipIntegration() {
        _navigationEvent.postValue(IntegrationFragmentDirections.actionGlobalHomeFragment())
    }
}
