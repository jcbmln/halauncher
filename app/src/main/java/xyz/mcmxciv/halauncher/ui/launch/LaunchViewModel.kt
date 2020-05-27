package xyz.mcmxciv.halauncher.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent
import xyz.mcmxciv.halauncher.authentication.AuthenticationUseCase
import xyz.mcmxciv.halauncher.domain.settings.SettingsUseCase
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    private val _navigationEvent = LiveEvent<NavDirections>().also { event ->
        val action = when {
            !settingsUseCase.instanceUrlSet ->
                LaunchFragmentDirections.actionLaunchFragmentToSetupNavigationGraph()
            !authenticationUseCase.verifyAuthentication() ->
                LaunchFragmentDirections.actionLaunchFragmentToAuthenticationNavigationGraph()
            else -> LaunchFragmentDirections.actionLaunchFragmentToHomeFragment()
        }

        event.postValue(action)
    }
    val navigationEvent: LiveData<NavDirections> = _navigationEvent
}
