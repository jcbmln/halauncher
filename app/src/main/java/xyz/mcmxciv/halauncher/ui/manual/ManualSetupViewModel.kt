package xyz.mcmxciv.halauncher.ui.manual

import android.view.inputmethod.EditorInfo
import xyz.mcmxciv.halauncher.settings.SettingsUseCase
import xyz.mcmxciv.halauncher.ui.BaseViewModel
import javax.inject.Inject

class ManualSetupViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : BaseViewModel() {

    fun onDiscoveryModeButtonClicked() {
        navigationEvent.postValue(
            ManualSetupFragmentDirections.actionManualSetupFragmentToDiscoveryFragment()
        )
    }

    fun onUrlSubmitted(url: String) {
        settingsUseCase.saveInstanceUrl(url)
    }

    fun onEditorAction(actionId: Int, value: String): Boolean =
        if (actionId == EditorInfo.IME_ACTION_GO) {
            onUrlSubmitted(value)
            true
        } else false
}
