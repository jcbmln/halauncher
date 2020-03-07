package xyz.mcmxciv.halauncher.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Sensor(
    @Json(name = "unique_id")
    val uniqueId: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "state")
    val state: Any,
    @Json(name = "icon")
    val icon: String,
    @Json(name = "attributes")
    val attributes: Map<String, Any>
) : SerializableModel() {
    companion object : SerializerObject<Sensor>()
}