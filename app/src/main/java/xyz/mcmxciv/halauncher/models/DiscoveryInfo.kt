package xyz.mcmxciv.halauncher.models

import xyz.mcmxciv.halauncher.data.models.SerializerObject
import xyz.mcmxciv.halauncher.data.models.SerializableModel

data class DiscoveryInfo(
    val baseUrl: String,
    val locationName: String,
    val requiresApiPassword: Boolean,
    val version: String
) : SerializableModel() {
    companion object : SerializerObject<DiscoveryInfo>()
}