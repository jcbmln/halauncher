package xyz.mcmxciv.halauncher.models

data class DiscoveryInfo(
    val baseUrl: String,
    val locationName: String,
    val requiresApiPassword: Boolean,
    val version: String
) : Model() {
    companion object : JsonModel<DiscoveryInfo>()
}