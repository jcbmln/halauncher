package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentSettingsBinding
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
            findPreference(CONNECTION_PREFERENCE_KEY),
            MainPreferencesFragmentDirections
                .actionMainPreferencesFragmentToConnectionPreferencesFragment()
        )
        addClickListener(
            findPreference(INTEGRATION_PREFERENCE_KEY),
            MainPreferencesFragmentDirections
                .actionMainPreferencesFragmentToIntegrationPreferencesFragment()
        )
        addClickListener(
            findPreference(DISPLAY_PREFERENCE_KEY),
            MainPreferencesFragmentDirections
                .actionMainPreferencesFragmentToDisplayPreferencesFragment()
        )
        addClickListener(
            findPreference(ABOUT_PREFERENCE_KEY),
            MainPreferencesFragmentDirections
                .actionMainPreferencesFragmentToAboutPreferencesFragment()
        )
    }

    companion object {
        private const val CONNECTION_PREFERENCE_KEY = "connection_preference"
        private const val INTEGRATION_PREFERENCE_KEY = "integration_preference"
        private const val DISPLAY_PREFERENCE_KEY = "display_preference"
        private const val ABOUT_PREFERENCE_KEY = "about_preference"
    }
}