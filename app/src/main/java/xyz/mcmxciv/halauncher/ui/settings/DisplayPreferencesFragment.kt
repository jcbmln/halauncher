package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.createViewModel

@Suppress("unused")
class DisplayPreferencesFragment : LauncherPreferenceFragment(), Preference.OnPreferenceChangeListener {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.connection_preferences, rootKey)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return true
    }
}