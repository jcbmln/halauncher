package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import android.view.View
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.views.ActionPreference

class ConnectionPreferencesFragment : LauncherPreferenceFragment(),
    Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.preference_connection_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.connection_preferences, rootKey)

        val connectionUrlPreference = findPreference<EditTextPreference>(CONNECTION_URL_KEY)
        connectionUrlPreference?.summary = viewModel.homeAssistantUrl
        connectionUrlPreference?.setDefaultValue(viewModel.homeAssistantUrl)
        connectionUrlPreference?.onPreferenceChangeListener = this

        val revokeTokenPreference = findPreference<ActionPreference>(REVOKE_TOKEN_KEY)
        revokeTokenPreference?.layoutResource = R.layout.preference_action
        revokeTokenPreference?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            CONNECTION_URL_KEY -> {
                viewModel.homeAssistantUrl = newValue.toString()
                viewModel.revokeToken()
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
        private const val CONNECTION_URL_KEY = "connection_url"
        private const val REVOKE_TOKEN_KEY = "revoke_token"
    }
}
