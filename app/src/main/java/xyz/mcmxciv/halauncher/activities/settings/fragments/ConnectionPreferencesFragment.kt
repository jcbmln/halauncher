package xyz.mcmxciv.halauncher.activities.settings.fragments

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.settings.SettingsViewModel
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BasePreferenceFragment

@Suppress("unused")
class ConnectionPreferencesFragment : BasePreferenceFragment(), Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.connection_preferences, rootKey)

        val internalUrlPreference = findPreference<EditTextPreference>(INTERNAL_URL_KEY)
        internalUrlPreference?.summary = viewModel.appSettings.url

        val externalUrlPrefernce = findPreference<EditTextPreference>(EXTERNAL_URL_KEY)
        externalUrlPrefernce?.summary = viewModel.appSettings.url

//        val revokeTokenPreference = findPreference<Preference>(REVOKE_TOKEN_KEY)
//        revokeTokenPreference?.layoutResource = R.layout.action_preference
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            INTERNAL_URL_KEY -> {
                viewModel.appSettings.url = newValue.toString()
                preference.summary = newValue.toString()
            }
            EXTERNAL_URL_KEY -> {
                viewModel.appSettings.url = newValue.toString()
                preference.summary = newValue.toString()
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