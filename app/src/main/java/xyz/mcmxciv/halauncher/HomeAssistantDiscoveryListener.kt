package xyz.mcmxciv.halauncher

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class HomeAssistantDiscoveryListener(
    callback: HomeAssistantResolveListener.Callback,
    private val nsdManager: NsdManager
) : NsdManager.DiscoveryListener {
    private val resolveListener = HomeAssistantResolveListener(callback)

    override fun onServiceFound(serviceInfo: NsdServiceInfo) {
        if (serviceInfo.serviceType == SERVICE_TYPE) {
            nsdManager.resolveService(serviceInfo, resolveListener)
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

    companion object {
        const val TAG = "HomeAssistantDiscoveryListener"
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}