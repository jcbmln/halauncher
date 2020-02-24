package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Config(
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double,
    @Json(name = "elevation")
    val elevation: Double,
    @Json(name = "unit_system")
    val unitSystem: Map<String, String>,
    @Json(name = "location_name")
    val locationName: String,
    @Json(name = "time_zone")
    val timeZone: String,
    @Json(name = "components")
    val components: List<String>,
    @Json(name = "version")
    val version: String,
    @Json(name = "theme_color")
    val themeColor: String
) : Model() {
    companion object : JsonModel<Config>()
}