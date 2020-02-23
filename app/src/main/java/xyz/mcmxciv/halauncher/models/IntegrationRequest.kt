package xyz.mcmxciv.halauncher.models

data class IntegrationRequest<T>(
    val type: String,
    val data: T?
)