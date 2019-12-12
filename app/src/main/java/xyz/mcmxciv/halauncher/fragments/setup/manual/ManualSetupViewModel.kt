package xyz.mcmxciv.halauncher.fragments.setup.manual

import androidx.lifecycle.ViewModel
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Inject

class ManualSetupViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {
    fun setUrl(url: String) {
        appSettings.url = url
    }
}