package xyz.mcmxciv.halauncher.integration.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebhookRequest(
    val type: String,
    val data: Any?
)
