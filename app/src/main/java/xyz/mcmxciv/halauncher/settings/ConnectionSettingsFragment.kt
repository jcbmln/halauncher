package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import xyz.mcmxciv.halauncher.R

@AndroidEntryPoint
class ConnectionSettingsFragment : BasePreferenceFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = getString(R.string.connection_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_screen_connections)
    }
}
