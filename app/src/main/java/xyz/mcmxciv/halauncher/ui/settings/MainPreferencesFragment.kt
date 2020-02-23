package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_settings.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.createViewModel

class MainPreferencesFragment : LauncherPreferenceFragment() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel { component.settingsViewModel() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.preference_top_level)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.main_preferences)

        addClickListener(
            findPreference(getString(R.string.preference_connection_key)),
            MainPreferencesFragmentDirections.actionMainPreferencesFragmentToConnectionPreferencesFragment()
        )
        addClickListener(
            findPreference(getString(R.string.preference_integration_key)),
            MainPreferencesFragmentDirections.actionMainPreferencesFragmentToIntegrationPreferencesFragment()
        )
        addClickListener(
            findPreference(getString(R.string.preference_display_key)),
            MainPreferencesFragmentDirections.actionMainPreferencesFragmentToDisplayPreferencesFragment()
        )
        addClickListener(
            findPreference(getString(R.string.preference_about_key)),
            MainPreferencesFragmentDirections.actionMainPreferencesFragmentToAboutPreferencesFragment()
        )
    }

}