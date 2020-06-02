package xyz.mcmxciv.halauncher.discovery

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DiscoveryManager @Inject constructor(
    private val nsdManager: NsdManager
) {
    private var discoveryStarted = false
    private val _instances = mutableListOf<HomeAssistantInstance>()

    private val instanceChannel by lazy {
        ConflatedBroadcastChannel<List<HomeAssistantInstance>>().also {
            it.offer(_instances)
        }
    }

    @FlowPreview
    val instances: Flow<List<HomeAssistantInstance>>
        get() = instanceChannel.asFlow()

    fun startDiscovery() {
        if (!discoveryStarted) {
            nsdManager.discoverServices(
                SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener
            )
            discoveryStarted = true
        }
    }

    fun stopDiscovery() {
        if (discoveryStarted) {
            nsdManager.stopServiceDiscovery(discoveryListener)
            discoveryStarted = false
        }
        instanceChannel.close()
    }

    private val discoveryListener: NsdManager.DiscoveryListener =
        object : NsdManager.DiscoveryListener {
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                nsdManager.resolveService(serviceInfo, resolveListener)
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Timber.e("Discovery failed: Error code:$errorCode")
            }

            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Timber.e("Discovery failed: Error code:$errorCode")
                nsdManager.stopServiceDiscovery(this)
                discoveryStarted = false
            }

            override fun onDiscoveryStarted(serviceType: String?) {
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                discoveryStarted = false
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                _instances.removeIf { i -> i.hostName == serviceInfo.host.hostAddress }
                instanceChannel.offer(_instances)
            }
        }

    private val resolveListener: NsdManager.ResolveListener
        get() = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Timber.e("Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                serviceInfo.attributes["base_url"]?.also { baseUrl ->
                    val instance = HomeAssistantInstance(
                        serviceInfo.serviceName,
                        baseUrl.toString(Charsets.UTF_8)
                    )
                    _instances.add(instance)
                    instanceChannel.offer(_instances)
                }
            }
        }

    companion object {
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}
