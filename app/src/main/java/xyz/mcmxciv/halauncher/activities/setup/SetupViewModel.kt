package xyz.mcmxciv.halauncher.activities.setup

import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.mcmxciv.halauncher.AppModel
import kotlin.reflect.KProperty

class SetupViewModel : ViewModel() {
    val setupMode: MutableLiveData<AppModel.SetupMode> by lazy {
        MutableLiveData<AppModel.SetupMode>()
    }
}
