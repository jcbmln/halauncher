package xyz.mcmxciv.halauncher.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent
import xyz.mcmxciv.halauncher.data.cache.LocalCache
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    private val localCache: LocalCache
) : ViewModel() {
    private val _navigationEvent = LiveEvent<NavDirections>().also { event ->
        val action = when {
            !localCache.hasHomeAssistantInstance ->
                LaunchFragmentDirections.actionLaunchFragmentToSetupNavigationGraph()
            !localCache.isAuthenticated ->
                LaunchFragmentDirections.actionLaunchFragmentToAuthenticationNavigationGraph()
            else -> LaunchFragmentDirections.actionLaunchFragmentToHomeFragment()
        }

        event.postValue(action)
    }
    val navigationEvent: LiveData<NavDirections> = _navigationEvent
}
