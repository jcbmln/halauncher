package xyz.mcmxciv.halauncher.manualsetup

import android.view.inputmethod.EditorInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.settings.SettingsUseCase
import javax.inject.Inject

@HiltViewModel
class ManualSetupViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : BaseViewModel() {

    fun onDiscoveryButtonClicked() {
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
