package xyz.mcmxciv.halauncher.ui.onboarding.manual

import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent
import xyz.mcmxciv.halauncher.domain.settings.SettingsUseCase
import javax.inject.Inject

class ManualSetupViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {
    private val _navigationEvent = LiveEvent<NavDirections>()
    val navigationEvent: LiveEvent<NavDirections> = _navigationEvent

    fun discoveryModeButtonClicked() =
        _navigationEvent.postValue(
            ManualSetupFragmentDirections.actionManualSetupFragmentToDiscoveryFragment()
        )

    fun urlSubmitted(url: String, actionId: Int = -1): Boolean =
        if (actionId == EditorInfo.IME_ACTION_GO) {
            settingsUseCase.saveInstanceUrl(url)
            _navigationEvent.postValue(
                ManualSetupFragmentDirections.actionGlobalAuthenticationNavigationGraph()
            )
            true
        } else false
}
