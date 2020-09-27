package xyz.mcmxciv.halauncher.launch

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.authentication.AuthenticationUseCase
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase
import xyz.mcmxciv.halauncher.settings.SettingsUseCase

class LaunchViewModel @ViewModelInject constructor(
    settingsUseCase: SettingsUseCase,
    authenticationUseCase: AuthenticationUseCase,
    integrationUseCase: IntegrationUseCase
) : BaseViewModel() {
    init {
        viewModelScope.launch {
            val action = when (false) {
                settingsUseCase.validateInstance() ->
                    LaunchFragmentDirections.actionLaunchFragmentToDiscoveryFragment()
                authenticationUseCase.validateAuthentication() ->
                    LaunchFragmentDirections.actionLaunchFragmentToAuthenticationFragment()
                integrationUseCase.validateIntegration() ->
                    LaunchFragmentDirections.actionLaunchFragmentToIntegrationFragment()
                else -> LaunchFragmentDirections.actionLaunchFragmentToHomeFragment()
            }

            navigationEvent.postValue(action)
        }
    }
}
