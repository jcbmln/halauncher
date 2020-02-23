package xyz.mcmxciv.halauncher.models

data class DomainServices(
    val domain: String,
    val services: List<String>
) : Model()