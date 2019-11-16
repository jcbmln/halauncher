package xyz.mcmxciv.halauncher

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class HomeAssistantResolveListener(
    private val listener: OnServiceResolvedListener
) : NsdManager.ResolveListener {
    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        Log.e(TAG, "Resolve failed: $errorCode")
    }

    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
        Log.i(TAG, "Resolve Succeeded. $serviceInfo")
        listener.onServiceResolved(serviceInfo)
    }

    interface OnServiceResolvedListener {
        fun onServiceResolved(serviceInfo: NsdServiceInfo)
    }

    companion object {
        private const val TAG = "HomeAssistantResolveListener"
    }
}