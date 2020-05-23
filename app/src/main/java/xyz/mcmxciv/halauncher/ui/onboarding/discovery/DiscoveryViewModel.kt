package xyz.mcmxciv.halauncher.ui.onboarding.discovery

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent
import timber.log.Timber
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.domain.settings.SettingsUseCase
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class DiscoveryViewModel @Inject constructor(
    private val nsdManager: NsdManager,
    private val resourceProvider: ResourceProvider,
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {
    private val _servicesList = mutableListOf<NsdServiceInfo>()
    private val _services = MutableLiveData<List<NsdServiceInfo>>()
    val services: LiveData<List<NsdServiceInfo>> = _services

    private var discoveryStarted: Boolean = false

    private val _navigationEvent = LiveEvent<NavDirections>()
    val navigationEvent: LiveEvent<NavDirections> = _navigationEvent

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _showServices = MutableLiveData<Boolean>()
    val showServices: LiveData<Boolean> = _showServices

    private val _headerText = MutableLiveData<String>()
    val headerText: LiveData<String> = _headerText

    init {
        setHeaderText(true)
        startDiscovery()
    }

    fun serviceSelected(url: String) {
        stopDiscovery()
        settingsUseCase.saveInstanceUrl(url)
    }

    fun manualSetupModeButtonClicked() =
        _navigationEvent.postValue(
            DiscoveryFragmentDirections.actionDiscoveryFragmentToManualSetupFragment()
        )

    private fun startDiscovery() {
        if (!discoveryStarted) {
            nsdManager.discoverServices(
                SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener
            )
            discoveryStarted = true
        }
    }

    private fun stopDiscovery() {
        if (discoveryStarted) {
            nsdManager.stopServiceDiscovery(discoveryListener)
            discoveryStarted = false
        }
    }

    private fun setHeaderText(emptyList: Boolean) {
        val text = if (emptyList) resourceProvider.getString(R.string.setup_discovery_text)
            else resourceProvider.getString(R.string.setup_selection_text)
        _headerText.postValue(text)
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            if (serviceInfo.serviceType == SERVICE_TYPE) {
                nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        Timber.e("Resolve failed: $errorCode")
                    }

                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        _servicesList.add(serviceInfo)
                        val isEmpty = _servicesList.count() > 0
                        _services.postValue(_servicesList)
                        setHeaderText(isEmpty)
                        _showProgress.postValue(isEmpty)
                        _showServices.postValue(!isEmpty)
                    }
                })
            }
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Timber.e("Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Timber.e("Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
            discoveryStarted = false
        }

        override fun onDiscoveryStarted(serviceType: String) {
        }

        override fun onDiscoveryStopped(serviceType: String) {
            discoveryStarted = false
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            _servicesList.remove(serviceInfo)
            _services.postValue(_servicesList)
        }
    }

    companion object {
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}
