package xyz.mcmxciv.halauncher.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import xyz.mcmxciv.halauncher.data.LocalCache
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    private val localCache: LocalCache
) : ViewModel() {
    private val launchStateEvent = LiveEvent<LaunchState>().also { event ->
        val launchState = when {
            !localCache.hasHomeAssistantInstance -> LaunchState.FIRST_LAUNCH
            !localCache.isAuthenticated -> LaunchState.UNAUTHENTICATED
            else -> LaunchState.AUTHENTICATED
        }
        event.postValue(launchState)
    }
    val launchState: LiveData<LaunchState> = launchStateEvent
}
