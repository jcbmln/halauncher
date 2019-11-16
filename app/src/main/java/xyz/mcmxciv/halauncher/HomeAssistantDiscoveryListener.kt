package xyz.mcmxciv.halauncher

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.fragments.DiscoveryFragment
import xyz.mcmxciv.halauncher.fragments.DiscoveryViewModel
import kotlin.collections.ArrayList

class HomeAssistantDiscoveryListener(
    private val nsdManager: NsdManager,
    fragment: DiscoveryFragment
) : NsdManager.DiscoveryListener {

    private val viewModel = ViewModelProviders.of(fragment).get(DiscoveryViewModel::class.java)

    override fun onServiceFound(serviceInfo: NsdServiceInfo) {
        if (serviceInfo.serviceType == SERVICE_TYPE) {
            Log.i(TAG, "Service found: ${serviceInfo.serviceName}")
            val serviceList = viewModel.services.value ?: ArrayList()
            serviceList.add(serviceInfo)
            viewModel.services.postValue(serviceList)
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