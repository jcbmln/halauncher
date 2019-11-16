package xyz.mcmxciv.halauncher.fragments

import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiscoveryViewModel : ViewModel() {
    val services: MutableLiveData<MutableList<NsdServiceInfo>> by lazy {
        MutableLiveData<MutableList<NsdServiceInfo>>()
    }

    val selectedService: MutableLiveData<NsdServiceInfo> by lazy {
        MutableLiveData<NsdServiceInfo>()
    }
}
