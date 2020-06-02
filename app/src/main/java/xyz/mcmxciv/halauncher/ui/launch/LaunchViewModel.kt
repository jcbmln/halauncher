package xyz.mcmxciv.halauncher.ui.launch

import xyz.mcmxciv.halauncher.settings.SettingsUseCase
import xyz.mcmxciv.halauncher.ui.BaseViewModel
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
