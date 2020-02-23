package xyz.mcmxciv.halauncher.models

data class DeviceIntegration(
    val cloudhookUrl: String?,
    val remoteUiUrl: String?,
    val secret: String?,
    val webhookId: String
) : Model()