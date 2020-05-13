package xyz.mcmxciv.halauncher.ui.setup.discovery

import android.net.nsd.NsdServiceInfo

interface ServiceSelectedListener {
    fun onServiceSelected(url: String)
}