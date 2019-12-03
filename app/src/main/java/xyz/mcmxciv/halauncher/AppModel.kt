package xyz.mcmxciv.halauncher

import android.content.Context
import android.net.nsd.NsdManager
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.utils.ContextInstance
import xyz.mcmxciv.halauncher.utils.AppPreferences

class AppModel(context: Context) {
    val idp = InvariantDeviceProfile.getInstance(context)
    val prefs = AppPreferences.getInstance(context)
    val nsdManager: NsdManager =
        SystemServiceInstance(NsdManager::class.java).getInstance(context) as NsdManager

    enum class SetupMode {
        DISCOVERY,
        MANUAL
    }

    companion object : ContextInstance<AppModel>(::AppModel)
}