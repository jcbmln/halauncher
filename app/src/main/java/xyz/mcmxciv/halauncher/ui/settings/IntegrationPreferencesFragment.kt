package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.createViewModel

@Suppress("unused")
class IntegrationPreferencesFragment : LauncherPreferenceFragment(),
    Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModelProvider().get() }
        setPreferencesFromResource(R.xml.integration_preferences, rootKey)

        val deviceNamePreference = findPreference<EditTextPreference>(DEVICE_NAME_KEY)
        deviceNamePreference?.summary = viewModel.deviceName

        val intervalArray = resources.getStringArray(R.array.sensor_update_interval_keys)
        val intervalValueArray = resources.getIntArray(R.array.sensor_update_interval_values)
        val index = intervalValueArray.indexOf(viewModel.sensorUpdateInterval.toInt())
        val sensorUpdateIntervalPreference =
            findPreference<ListPreference>(SENSOR_UPDATE_INTERVAL_KEY)
        sensorUpdateIntervalPreference?.summary = intervalArray[index]
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            DEVICE_NAME_KEY -> {
                viewModel.deviceName = newValue.toString()
                preference.summary = newValue.toString()
            }
            SENSOR_UPDATE_INTERVAL_KEY -> {
                viewModel.sensorUpdateInterval = newValue as Long
                preference.summary = (preference as ListPreference).entry
            }
        }

        return true
    }

    companion object {
        private const val DEVICE_NAME_KEY = "device_name"
        private const val SENSOR_UPDATE_INTERVAL_KEY = "sensor_update_interval"
    }
}
