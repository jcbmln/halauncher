package xyz.mcmxciv.halauncher.integration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.HalauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.device.DeviceManager
import javax.inject.Inject

class IntegrationViewModel @Inject constructor(
    private val deviceManager: DeviceManager,
    private val integrationUseCase: IntegrationUseCase
) : BaseViewModel() {
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

    private fun registerDevice() {
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            errorEvent.postValue(R.string.device_registration_error)
            _buttonVisibility.postValue(true)
            _progressVisibility.postValue(false)
        }

        viewModelScope.launch(exceptionHandler) {
            integrationUseCase.registerDevice(deviceManager.deviceInfo)
            HalauncherApplication.instance.startWorkers()
            navigationEvent.postValue(
                IntegrationFragmentDirections.actionIntegrationFragmentToHomeFragment()
            )
        }
    }

    fun retry() {
        _buttonVisibility.postValue(false)
        _progressVisibility.postValue(true)
        registerDevice()
    }

    fun skip() {
        integrationUseCase.optOut()
        navigationEvent.postValue(
            IntegrationFragmentDirections.actionIntegrationFragmentToHomeFragment()
        )
    }
}
