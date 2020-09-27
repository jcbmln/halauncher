package xyz.mcmxciv.halauncher.settings

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeAssistantInstance(
    val name: String,
    val baseUrl: String,
    val version: String
)
