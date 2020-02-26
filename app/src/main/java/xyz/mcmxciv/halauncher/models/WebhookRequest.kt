package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebhookRequest(
    val type: String,
    val data: Any?
)