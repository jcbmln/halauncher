package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import android.view.View
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment

class HomeAssistantPreferencesFragment : LauncherPreferenceFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.pt_homeassistant)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.home_assistant_preferences, rootKey)

        // TODO: Restart SensorUpdateWorker when option changed
    }
}