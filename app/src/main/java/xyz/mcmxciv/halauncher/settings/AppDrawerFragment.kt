package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.View
import xyz.mcmxciv.halauncher.R

class AppDrawerFragment : BasePreferenceFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.app_drawer_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_screen_app_drawer)
    }
}
