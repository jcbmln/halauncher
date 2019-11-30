package xyz.mcmxciv.halauncher.interfaces

import android.net.nsd.NsdServiceInfo

interface ServiceSelectedListener {
    fun onServiceSelected(serviceInfo: NsdServiceInfo)
}