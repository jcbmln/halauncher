package xyz.mcmxciv.halauncher.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SensorRegistration(
    @Json(name = "unique_id")
    val uniqueId: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "state")
    val state: Any,
    @Json(name = "icon")
    val icon: String,
    @Json(name = "attributes")
    val attributes: Map<String, Any>,
    @Json(name = "name")
    val name: String,
    @Json(name = "device_class")
    val deviceClass: String? = null,
    @Json(name = "unit_of_measurement")
    val unitOfMeasurement: String? = null
) : SerializableModel() {
    constructor(
        sensor: Sensor,
        name: String,
        deviceClass: String? = null,
        unitOfMeasurement: String? = null
    ) : this(
        sensor.uniqueId,
        sensor.type,
        sensor.state,
        sensor.icon,
        sensor.attributes,
        name,
        deviceClass,
        unitOfMeasurement
    )

    companion object : SerializerObject<Sensor>()
}