package xyz.mcmxciv.halauncher.ui.setup

import android.net.nsd.NsdServiceInfo

interface ServiceSelectedListener {
    fun onServiceSelected(serviceInfo: NsdServiceInfo)
}