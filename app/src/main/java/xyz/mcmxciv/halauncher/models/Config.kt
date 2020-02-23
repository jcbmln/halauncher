package xyz.mcmxciv.halauncher.models

data class Config(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val unitSystem: HashMap<String, String>,
    val locationName: String,
    val timeZone: String,
    val components: Array<String>,
    val version: String,
    val themeColor: String
) : Model() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Config

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (elevation != other.elevation) return false
        if (unitSystem != other.unitSystem) return false
        if (locationName != other.locationName) return false
        if (timeZone != other.timeZone) return false
        if (!components.contentEquals(other.components)) return false
        if (version != other.version) return false
        if (themeColor != other.themeColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + elevation.hashCode()
        result = 31 * result + unitSystem.hashCode()
        result = 31 * result + locationName.hashCode()
        result = 31 * result + timeZone.hashCode()
        result = 31 * result + components.contentHashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + themeColor.hashCode()
        return result
    }

    companion object : JsonModel<Config>()
}