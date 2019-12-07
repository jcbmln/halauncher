package xyz.mcmxciv.halauncher.activities.setup.integration

import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.repositories.IntegrationRepository
import xyz.mcmxciv.halauncher.utils.BaseViewModel
import javax.inject.Inject

class IntegrationViewModel : BaseViewModel() {
    @Inject lateinit var authenticationRepository: AuthenticationRepository
    @Inject lateinit var integrationRepository: IntegrationRepository

    val integrationState: MutableLiveData<IntegrationState> by lazy {
        MutableLiveData<IntegrationState>()
    }

    val integrationError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

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
            Build.DEVICE,
            Build.MANUFACTURER,
            Build.MODEL,
            "Android",
            Build.VERSION.SDK_INT.toString(),
            false,
            null
        )

        viewModelScope.launch(integrationExceptionHandler) {
            val bearerToken = authenticationRepository.bearerToken()
            integrationRepository.registerDevice(bearerToken, deviceRegistration)
            integrationState.value = IntegrationState.SUCCESS
        }
    }

    enum class IntegrationState {
        SUCCESS,
        FAILED
    }

    companion object {
        private const val TAG = "IntegrationViewModel"
    }
}
