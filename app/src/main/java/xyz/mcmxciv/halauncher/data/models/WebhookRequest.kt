package xyz.mcmxciv.halauncher.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebhookRequest(
    val type: String,
    val data: Any?
)
