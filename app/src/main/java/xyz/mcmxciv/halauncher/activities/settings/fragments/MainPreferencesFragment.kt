package xyz.mcmxciv.halauncher.activities.settings.fragments

import android.os.Bundle
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.settings.SettingsViewModel
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BasePreferenceFragment

class MainPreferencesFragment : BasePreferenceFragment() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }
}