package xyz.mcmxciv.halauncher.ui.setup

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.ViewModel
import timber.log.Timber
import xyz.mcmxciv.halauncher.utils.AppSettings
import xyz.mcmxciv.halauncher.utils.ResourceLiveData
import javax.inject.Inject

class SetupViewModel @Inject constructor(
    private val nsdManager: NsdManager,
    private val appSettings: AppSettings
) : ViewModel() {
    private var services: MutableList<NsdServiceInfo> = ArrayList()

    val servicesData = ResourceLiveData<List<NsdServiceInfo>>()
    val resolvedUrl = ResourceLiveData<String>()

    private var discoveryStarted: Boolean = false

    fun startDiscovery() {
        if (!discoveryStarted) {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            discoveryStarted = true
        }
    }

    fun stopDiscovery() {
        if (discoveryStarted) nsdManager.stopServiceDiscovery(discoveryListener)
    }

    fun resolveService(serviceInfo: NsdServiceInfo) {
        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Timber.e("Resolve failed: $errorCode")
                resolvedUrl.postError("Could not resolve service.")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                val url = "http://${serviceInfo.host.hostAddress}:${serviceInfo.port}"
                resolvedUrl.postSuccess(url)
            }
        }

        resolvedUrl.postLoading()
        nsdManager.resolveService(serviceInfo, resolveListener)
    }

    fun setUrl(url: String) {
        appSettings.url = url
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            if (serviceInfo.serviceType == SERVICE_TYPE) {
                services.add(serviceInfo)
                servicesData.postSuccess(services)
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
            servicesData.postLoading()
        }

        override fun onDiscoveryStopped(serviceType: String) {
            servicesData.postLoading()
            discoveryStarted = false
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            services.remove(serviceInfo)
            servicesData.postSuccess(services)
        }
    }

    companion object {
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}