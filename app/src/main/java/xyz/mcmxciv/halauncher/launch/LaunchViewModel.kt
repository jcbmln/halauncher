package xyz.mcmxciv.halauncher.launch

import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.settings.SettingsUseCase
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : BaseViewModel() {
    init {
        if (!settingsUseCase.hasHassInstance) {
            navigationEvent.postValue(
                LaunchFragmentDirections.actionLaunchFragmentToDiscoveryFragment()
            )
        }
    }
}
