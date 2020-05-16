package xyz.mcmxciv.halauncher.ui.setup

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import xyz.mcmxciv.halauncher.LocalStorage
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import javax.inject.Inject

class SetupViewModel @Inject constructor(
    private val localStorage: LocalStorage,
    private val nsdManager: NsdManager
) : ViewModel() {
    private var services = mutableListOf<NsdServiceInfo>()
    val servicesData = MutableLiveData<List<NsdServiceInfo>>()

    private var discoveryStarted: Boolean = false

    fun startDiscovery() {
        if (!discoveryStarted) {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            discoveryStarted = true
        }
    }

    fun stopDiscovery() {
        if (discoveryStarted) {
            nsdManager.stopServiceDiscovery(discoveryListener)
            discoveryStarted = false
        }
    }

    fun setUrl(url: String) {
        localStorage.baseUrl = url
    }

    override fun onCleared() {
        super.onCleared()
        stopDiscovery()
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            if (serviceInfo.serviceType == SERVICE_TYPE) {
                nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        Timber.e("Resolve failed: $errorCode")
                    }

                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        services.add(serviceInfo)
                        servicesData.postValue(services)
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
            services.remove(serviceInfo)
            servicesData.postValue(services)
        }
    }

    companion object {
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}