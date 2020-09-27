package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.View
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.navigate
import xyz.mcmxciv.halauncher.observe

class AppDrawerSettingsFragment : BasePreferenceFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.app_drawer_settings_title)

        observe(viewModel.navigation) { navigate(it) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_screen_app_drawer)

        findPreference<DropDownPreference>(SettingsRepository.APP_DRAWER_COLUMNS_KEY)?.also {
            it.entries = viewModel.appDrawerColumnOptionEntries
            it.entryValues = viewModel.appDrawerColumnOptions
            it.setDefaultValue(viewModel.appDrawerColumns)
        }

        findPreference<Preference>(R.string.hidden_apps_key)
            ?.setOnPreferenceClickListener { viewModel.onHiddenAppsSettingsSelected() }
    }
}
