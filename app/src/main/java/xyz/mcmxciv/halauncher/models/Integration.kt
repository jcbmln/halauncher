package xyz.mcmxciv.halauncher.models

data class Integration(
    val cloudhookUrl: String,
    val remoteUiUrl: String,
    val secret: String,
    val webhookId: String
)