package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.createViewModel

@Suppress("unused")
class IntegrationPreferencesFragment : LauncherPreferenceFragment(), Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.integration_preferences, rootKey)

        val localUrlPreference = findPreference<EditTextPreference>("local_url")
//        localUrlPreference?.summary = viewModel.launcherSettings.url
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            LOCAL_URL_KEY -> {
//                viewModel.launcherSettings.url = newValue.toString()
                preference.summary = newValue.toString()
            }
//            DEVICE_NAME_KEY -> {
//                viewModel.launcherSettings.deviceName = newValue.toString()
//                preference.summary = newValue.toString()
//            }
        }

        return true
    }

    companion object {
        private const val LOCAL_URL_KEY = "local_url"
        private const val DEVICE_NAME_KEY = "device_name"
    }
}