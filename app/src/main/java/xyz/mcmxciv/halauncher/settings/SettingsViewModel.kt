package xyz.mcmxciv.halauncher.settings

import androidx.hilt.lifecycle.ViewModelInject
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import java.lang.IllegalStateException

class SettingsViewModel @ViewModelInject constructor(
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {
    fun onCategorySelected(key: String) {
        val action = when (key) {
            resourceProvider.getString(R.string.connection_settings_key) ->
                SettingsFragmentDirections.actionSettingsFragmentToConnectionSettingsFragment()
            resourceProvider.getString(R.string.connection_settings_key) ->
                SettingsFragmentDirections.actionSettingsFragmentToAboutSettingsFragment()
            else -> throw IllegalStateException("This is not a valid preference key.")
        }

        navigationEvent.postValue(action)
    }
}
