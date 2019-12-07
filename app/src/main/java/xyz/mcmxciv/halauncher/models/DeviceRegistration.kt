package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class DeviceRegistration(
    @Json(name = "app_id")
    val appId: String,
    @Json(name = "app_name")
    val appName: String,
    @Json(name = "app_version")
    val appVersion: String,
    @Json(name = "device_name")
    val deviceName: String,
    @Json(name = "manufacturer")
    val manufacturer: String,
    @Json(name = "model")
    val model: String,
    @Json(name = "os_name")
    val osName: String,
    @Json(name = "os_version")
    val osVersion: String,
    @Json(name = "supports_encryption")
    val supportsEncryption: Boolean,
    @Json(name = "app_data")
    val appData: Map<String, String>?
)
