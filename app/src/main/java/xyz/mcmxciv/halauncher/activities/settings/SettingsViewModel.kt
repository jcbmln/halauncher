package xyz.mcmxciv.halauncher.activities.settings

import androidx.lifecycle.ViewModel
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {
}