package xyz.mcmxciv.halauncher.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import xyz.mcmxciv.halauncher.data.models.SerializableModel
import xyz.mcmxciv.halauncher.data.models.SerializerObject

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