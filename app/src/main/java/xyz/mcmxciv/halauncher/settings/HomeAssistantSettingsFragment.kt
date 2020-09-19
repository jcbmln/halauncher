package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.View
import androidx.preference.DropDownPreference
import androidx.preference.SwitchPreference
import dagger.hilt.android.AndroidEntryPoint
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.integration.IntegrationRepository

@AndroidEntryPoint
class HomeAssistantSettingsFragment : BasePreferenceFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.home_assistant_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_screen_home_assistant)

        findPreference<SwitchPreference>(IntegrationRepository.INTEGRATION_ENABLED_KEY)?.also {
            it.isEnabled = !viewModel.integrationEnabled
            it.setOnPreferenceChangeListener { preference, _ ->
                preference.isEnabled = false
                viewModel.onIntegrationPreferenceChanged()

                true
            }
        }

        findPreference<DropDownPreference>(IntegrationRepository.SENSOR_UPDATE_INTERVAL_KEY)?.also {
            it.setOnPreferenceChangeListener { _, _ ->
                viewModel.onSensorUpdateIntervalPreferenceChanged()
            }
        }
    }
}
