package xyz.mcmxciv.halauncher.models

data class DeviceRegistration(
    val appId: String? = null,
    val appName: String? = null,
    val appVersion: String? = null,
    val deviceName: String? = null,
    val manufacturer: String? = null,
    val model: String? = null,
    val osName: String? = null,
    val osVersion: String? = null,
    val supportsEncryption: Boolean? = false,
    val appData: Map<String, String>? = null,
    // Added in HA 0.104.0
    val deviceId: String? = null
) : Model() {
    companion object : JsonModel<DeviceRegistration>()
}