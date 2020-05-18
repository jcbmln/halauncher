package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.sensors.SensorUpdateWorker
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.main.MainActivityViewModel

class HomeAssistantPreferencesFragment
    : LauncherPreferenceFragment(), Preference.OnPreferenceChangeListener {
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.pt_homeassistant)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.home_assistant_preferences, rootKey)

        findPreference(R.string.pk_homeassistant_url)?.onPreferenceChangeListener = this
        findPreference(R.string.pk_sensor_update_interval)?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when(preference.key) {
            getString(R.string.pk_homeassistant_url) -> viewModel.revokeSession()
            getString(R.string.pk_sensor_update_interval) ->
                context?.let { SensorUpdateWorker.start(it, newValue as Long) }
        }

        return true
    }
}