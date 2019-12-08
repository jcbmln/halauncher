package xyz.mcmxciv.halauncher.fragments.setup.discovery

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception

class DiscoveryViewModel : ViewModel() {
    val services: MutableLiveData<MutableList<NsdServiceInfo>> = MutableLiveData()
    val resolvedUrl: MutableLiveData<String> = MutableLiveData()

    fun start(manager: NsdManager) {
        nsdManager = manager
    }

    fun startDiscovery() {
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        try {
            nsdManager.stopServiceDiscovery(discoveryListener)
        }
        catch (ex: Exception) {
            Log.e(TAG, ex.message ?: "An unexpected error occurred.")
        }
    }

    fun resolveService(serviceInfo: NsdServiceInfo) {
        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "Resolve Succeeded. $serviceInfo")
                val url = "http://${serviceInfo.host.hostAddress}:${serviceInfo.port}"
                resolvedUrl.postValue(url)
            }
        }

        nsdManager.resolveService(serviceInfo, resolveListener)
    }

    private lateinit var nsdManager: NsdManager

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            if (serviceInfo.serviceType == SERVICE_TYPE) {
                Log.i(TAG, "Service found: ${serviceInfo.serviceName}")
                val serviceList = services.value ?: ArrayList()
                serviceList.add(serviceInfo)
                services.postValue(serviceList)
            }
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onDiscoveryStarted(serviceType: String) {
            Log.i(TAG, "Discovery started: $serviceType")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Log.e(TAG, "Service lost: $serviceInfo")
        }
    }

    companion object {
        const val TAG = "DiscoveryViewModel"
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}
