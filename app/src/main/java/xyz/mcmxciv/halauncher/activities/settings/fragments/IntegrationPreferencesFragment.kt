package xyz.mcmxciv.halauncher.activities.settings.fragments

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.settings.SettingsViewModel
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BasePreferenceFragment

@Suppress("unused")
class IntegrationPreferencesFragment : BasePreferenceFragment(), Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.integration_preferences, rootKey)

        val localUrlPreference = findPreference<EditTextPreference>("local_url")
        localUrlPreference?.summary = viewModel.appSettings.url
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            LOCAL_URL_KEY -> {
                viewModel.appSettings.url = newValue.toString()
                preference.summary = newValue.toString()
            }
            DEVICE_NAME_KEY -> {
                viewModel.appSettings.deviceName = newValue.toString()
                preference.summary = newValue.toString()
            }
        }

        return true
    }

    companion object {
        private const val LOCAL_URL_KEY = "local_url"
        private const val DEVICE_NAME_KEY = "device_name"
    }
}