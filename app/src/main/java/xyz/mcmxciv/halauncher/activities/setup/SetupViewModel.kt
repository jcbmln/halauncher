package xyz.mcmxciv.halauncher.activities.setup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.mcmxciv.halauncher.AppModel

class SetupViewModel : ViewModel() {
    val setupMode: MutableLiveData<AppModel.SetupMode> by lazy {
        MutableLiveData<AppModel.SetupMode>().also {
            it.value = AppModel.SetupMode.DISCOVERY
        }
    }
}
