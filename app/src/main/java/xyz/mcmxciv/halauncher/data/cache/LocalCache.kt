package xyz.mcmxciv.halauncher.data.cache

import com.squareup.moshi.Moshi
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.Session
import xyz.mcmxciv.halauncher.domain.models.WebhookInfo
import xyz.mcmxciv.halauncher.ui.HassTheme

interface LocalCache {
    var instanceUrl: String
    var session: Session?
    var deviceInfo: DeviceInfo?
    var webhookInfo: WebhookInfo?
    var deviceName: String
    var sensorIds: Set<String>
    var sensorUpdateInterval: Int
    val instanceUrlSet: Boolean
    val isAuthenticated: Boolean
    var theme: HassTheme?
}

inline fun <reified T> serialize(obj: T): String {
    val adapter = Moshi.Builder().build().adapter(T::class.java)
    return adapter.toJson(obj)
}

inline fun <reified T> deserialize(value: String): T? {
    val adapter = Moshi.Builder().build().adapter(T::class.java)
    return adapter.fromJson(value)
}
