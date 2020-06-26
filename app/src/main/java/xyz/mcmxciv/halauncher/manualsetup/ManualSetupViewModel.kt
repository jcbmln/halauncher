package xyz.mcmxciv.halauncher.manualsetup

import android.view.inputmethod.EditorInfo
import androidx.hilt.lifecycle.ViewModelInject
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.settings.SettingsUseCase

class ManualSetupViewModel @ViewModelInject constructor(
    private val settingsUseCase: SettingsUseCase
) : BaseViewModel() {

    fun onDiscoveryModeButtonClicked() {
        navigationEvent.postValue(
            ManualSetupFragmentDirections.actionManualSetupFragmentToDiscoveryFragment()
        )
    }

    fun onUrlSubmitted(url: String) {
        settingsUseCase.saveInstanceUrl(url)
        navigationEvent.postValue(
            ManualSetupFragmentDirections.actionManualSetupFragmentToAuthenticationFragment()
        )
    }

    fun onEditorAction(actionId: Int, value: String): Boolean =
        if (actionId == EditorInfo.IME_ACTION_GO) {
            onUrlSubmitted(value)
            true
        } else false
}
