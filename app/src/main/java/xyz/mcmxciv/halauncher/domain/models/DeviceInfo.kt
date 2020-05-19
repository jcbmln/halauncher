package xyz.mcmxciv.halauncher.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import xyz.mcmxciv.halauncher.data.models.SerializableModel
import xyz.mcmxciv.halauncher.data.models.SerializerObject

@JsonClass(generateAdapter = true)
data class DeviceInfo(
    @Json(name = "app_id")
    val appId: String? = null,
    @Json(name = "app_name")
    val appName: String? = null,
    @Json(name = "app_version")
    val appVersion: String? = null,
    @Json(name = "device_name")
    val deviceName: String? = null,
    @Json(name = "manufacturer")
    val manufacturer: String? = null,
    @Json(name = "model")
    val model: String? = null,
    @Json(name = "os_name")
    val osName: String? = null,
    @Json(name = "os_version")
    val osVersion: String? = null,
    @Json(name = "supports_encryption")
    val supportsEncryption: Boolean? = false,
    @Json(name = "app_data")
    val appData: Map<String, String>? = null,
    // Added in HA 0.104.0
    @Json(name = "device_id")
    val deviceId: String? = null
) : SerializableModel() {
    companion object : SerializerObject<DeviceInfo>()
}
