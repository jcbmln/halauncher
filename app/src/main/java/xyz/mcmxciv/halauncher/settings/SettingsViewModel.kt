package xyz.mcmxciv.halauncher.settings

import androidx.hilt.lifecycle.ViewModelInject
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.utils.ResourceProvider

class SettingsViewModel @ViewModelInject constructor(
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {
    fun onCategorySelected(key: String) {
//        val action = when (key) {
//
//        }
    }
}
