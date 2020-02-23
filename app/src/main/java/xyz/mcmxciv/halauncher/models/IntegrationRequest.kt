package xyz.mcmxciv.halauncher.models

data class IntegrationRequest<T : Model>(
    val type: String,
    val data: T?
)