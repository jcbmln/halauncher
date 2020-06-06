package xyz.mcmxciv.halauncher.discovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import xyz.mcmxciv.halauncher.BaseViewModel
import xyz.mcmxciv.halauncher.settings.SettingsUseCase
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DiscoveryViewModel @Inject constructor(
    private val discoveryManager: DiscoveryManager,
    private val settingsUseCase: SettingsUseCase
) : BaseViewModel() {

    @FlowPreview
    private val _instances: LiveData<List<HomeAssistantInstance>> =
        discoveryManager.instances.asLiveData(viewModelScope.coroutineContext)
    @FlowPreview
    val instances: LiveData<List<HomeAssistantInstance>>
        get() = _instances

    init {
        discoveryManager.startDiscovery()
    }

    fun instanceSelected(instance: HomeAssistantInstance) {
        discoveryManager.stopDiscovery()
        settingsUseCase.saveInstanceUrl(instance.hostName)
        navigationEvent.postValue(
            DiscoveryFragmentDirections.actionDiscoveryFragmentToAuthenticationFragment()
        )
    }

    override fun onCleared() {
        super.onCleared()
        discoveryManager.stopDiscovery()
    }
}
