package xyz.mcmxciv.halauncher.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import xyz.mcmxciv.halauncher.LocalStorage
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    private val localStorage: LocalStorage
) : ViewModel() {
    private val launchStateEvent = LiveEvent<LaunchState>().also { event ->
        val launchState = when {
            !localStorage.hasHomeAssistantInstance -> LaunchState.FIRST_LAUNCH
            !localStorage.isAuthenticated -> LaunchState.UNAUTHENTICATED
            else -> LaunchState.AUTHENTICATED
        }
        event.postValue(launchState)
    }
    val launchState: LiveData<LaunchState> = launchStateEvent
}