package xyz.mcmxciv.halauncher.activities.settings

import androidx.lifecycle.ViewModel
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    val appSettings: AppSettings
) : ViewModel()