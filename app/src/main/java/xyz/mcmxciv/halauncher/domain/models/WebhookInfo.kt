package xyz.mcmxciv.halauncher.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import xyz.mcmxciv.halauncher.data.models.SerializableModel
import xyz.mcmxciv.halauncher.data.models.SerializerObject

@JsonClass(generateAdapter = true)
data class WebhookInfo(
    @Json(name = "cloudhook_url")
    val cloudhookUrl: String?,
    @Json(name = "remote_ui_url")
    val remoteUiUrl: String?,
    @Json(name = "secret")
    val secret: String?,
    @Json(name = "webhook_id")
    val webhookId: String
) : SerializableModel() {
    companion object : SerializerObject<WebhookInfo>()
}
