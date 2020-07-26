package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.View
import androidx.preference.SwitchPreference
import dagger.hilt.android.AndroidEntryPoint
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.integration.IntegrationRepository
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase

@AndroidEntryPoint
class ConnectionSettingsFragment : BasePreferenceFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.home_assistant_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_screen_connections)

        findPreference<SwitchPreference>(IntegrationRepository.INTEGRATION_OPT_IN_KEY)?.also {
            it.isEnabled = !viewModel.integrationEnabled
            it.setOnPreferenceChangeListener { preference, _ ->
                preference.isEnabled = false
                viewModel.enableIntegration()

                true
            }
        }
    }
}
