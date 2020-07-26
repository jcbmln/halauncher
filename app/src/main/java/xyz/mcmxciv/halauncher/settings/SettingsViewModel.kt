package xyz.mcmxciv.halauncher.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import java.lang.IllegalStateException

class SettingsViewModel @ViewModelInject constructor(
    private val integrationUseCase: IntegrationUseCase,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {
    val integrationEnabled: Boolean
        get() = integrationUseCase.integrationOptIn


    fun onCategorySelected(key: String) {
        val action = when (key) {
            resourceProvider.getString(R.string.home_assistant_settings_key) ->
                SettingsFragmentDirections.actionSettingsFragmentToConnectionSettingsFragment()
            resourceProvider.getString(R.string.home_assistant_settings_key) ->
                SettingsFragmentDirections.actionSettingsFragmentToAboutSettingsFragment()
            else -> throw IllegalStateException("This is not a valid preference key.")
        }

        navigationEvent.postValue(action)
    }

    fun enableIntegration() {
        viewModelScope.launch {
            integrationUseCase.optIn()
        }
    }
}
