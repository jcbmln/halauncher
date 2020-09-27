package xyz.mcmxciv.halauncher.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.HalauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.device.DeviceManager
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import java.lang.IllegalStateException

class SettingsViewModel @ViewModelInject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val integrationUseCase: IntegrationUseCase,
    private val deviceManager: DeviceManager,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {
    val integrationEnabled: Boolean
        get() = integrationUseCase.integrationEnabled

    val appDrawerColumns: Int
        get() = settingsUseCase.appDrawerColumns

    val appDrawerColumnOptions: Array<CharSequence>
        get() = settingsUseCase.iconColumnOptions
            .distinct()
            .map { option -> option.toString() }
            .toTypedArray()

    val appDrawerColumnOptionEntries: Array<CharSequence>
        get() = appDrawerColumnOptions.map { option -> "$option Columns" }.toTypedArray()

    fun onCategorySelected(key: String) {
        val action = when (key) {
            resourceProvider.getString(R.string.home_assistant_settings_key) ->
                SettingsFragmentDirections.actionSettingsFragmentToConnectionSettingsFragment()
            resourceProvider.getString(R.string.app_drawer_settings_key) ->
                SettingsFragmentDirections.actionSettingsFragmentToAppDrawerFragment()
            resourceProvider.getString(R.string.about_settings_key) ->
                SettingsFragmentDirections.actionSettingsFragmentToAboutSettingsFragment()
            else -> throw IllegalStateException("This is not a valid preference key.")
        }

        navigationEvent.postValue(action)
    }

    fun onPrivacyPolicySelected(): Boolean {
        navigationEvent.postValue(
            AboutSettingsFragmentDirections.actionAboutSettingsFragmentToPrivacyPolicyFragment()
        )
        return true
    }

    fun onHiddenAppsSettingsSelected(): Boolean {
        navigationEvent.postValue(
            AppDrawerSettingsFragmentDirections
                .actionAppDrawerSettingsFragmentToHiddenAppsFragment()
        )
        return true
    }

    fun onIntegrationPreferenceChanged() {
        viewModelScope.launch {
            integrationUseCase.registerDevice(deviceManager.deviceInfo)
        }
    }

    fun onSensorUpdateIntervalPreferenceChanged(): Boolean {
        HalauncherApplication.instance.startWorkers()
        return true
    }
}
