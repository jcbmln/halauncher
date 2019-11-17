package xyz.mcmxciv.halauncher

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.fragments.DiscoveryFragment
import xyz.mcmxciv.halauncher.fragments.DiscoveryViewModel

class HomeAssistantResolveListener(fragment: Fragment) : NsdManager.ResolveListener {
    private val viewModel = ViewModelProviders.of(fragment).get(DiscoveryViewModel::class.java)

    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        Log.e(TAG, "Resolve failed: $errorCode")
    }

    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
        Log.i(TAG, "Resolve Succeeded. $serviceInfo")
        val url = "http://${serviceInfo.host.hostAddress}:${serviceInfo.port}"
        viewModel.resolvedUrl.postValue(url)
    }

    companion object {
        private const val TAG = "HomeAssistantResolveListener"
    }
}