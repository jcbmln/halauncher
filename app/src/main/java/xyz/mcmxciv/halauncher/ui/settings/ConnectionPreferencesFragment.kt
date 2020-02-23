package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.createViewModel

@Suppress("unused")
class ConnectionPreferencesFragment : LauncherPreferenceFragment(), Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.connection_preferences, rootKey)

        val internalUrlPreference = findPreference<EditTextPreference>(INTERNAL_URL_KEY)
//        internalUrlPreference?.summary = viewModel.launcherSettings.url

        val externalUrlPreference = findPreference<EditTextPreference>(EXTERNAL_URL_KEY)
//        externalUrlPreference?.summary = viewModel.launcherSettings.url

//        val revokeTokenPreference = findPreference<Preference>(REVOKE_TOKEN_KEY)
//        revokeTokenPreference?.layoutResource = R.layout.preference_action
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            INTERNAL_URL_KEY -> {
//                viewModel.launcherSettings.url = newValue.toString()
                preference.summary = newValue.toString()
            }
            EXTERNAL_URL_KEY -> {
//                viewModel.launcherSettings.url = newValue.toString()
                preference.summary = newValue.toString()
            }
            REVOKE_TOKEN_KEY -> {
                val revokeToken = newValue as Boolean
                if (revokeToken) {
                    viewModel.revokeToken()
                }
            }
        }

        return true
    }

    companion object {
        private const val INTERNAL_URL_KEY = "internal_url"
        private const val EXTERNAL_URL_KEY = "external_url"
        private const val REVOKE_TOKEN_KEY = "revoke_token"
    }
}
