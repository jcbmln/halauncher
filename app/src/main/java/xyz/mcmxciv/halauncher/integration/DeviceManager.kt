package xyz.mcmxciv.halauncher.integration

import android.os.Build
import android.provider.Settings
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.integration.models.DeviceInfo
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class DeviceManager @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    val deviceInfo: DeviceInfo
        get() = DeviceInfo(
            BuildConfig.APPLICATION_ID,
            resourceProvider.getString(R.string.app_name),
            BuildConfig.VERSION_NAME,
            resourceProvider.getSettingsString("bluetooth_name") ?: Build.MODEL,
            Build.MANUFACTURER,
            Build.MODEL,
            "Android",
            Build.VERSION.SDK_INT.toString(),
            false,
            null,
            resourceProvider.getSettingsString(Settings.Secure.ANDROID_ID)
        )
}
