package xyz.mcmxciv.halauncher

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class HomeAssistantResolveListener(private val callback: Callback) : NsdManager.ResolveListener {
    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        Log.e(TAG, "Resolve failed: $errorCode")
    }

    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
        Log.i(TAG, "Resolve Succeeded. $serviceInfo")
        callback.openService(serviceInfo)
    }

    interface Callback {
        fun openService(serviceInfo: NsdServiceInfo)
    }

    companion object {
        private const val TAG = "HomeAssistantResolveListener"
    }
}