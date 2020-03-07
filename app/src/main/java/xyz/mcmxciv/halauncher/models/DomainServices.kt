package xyz.mcmxciv.halauncher.models

import xyz.mcmxciv.halauncher.data.models.SerializerObject
import xyz.mcmxciv.halauncher.data.models.SerializableModel

data class DomainServices(
    val domain: String,
    val services: List<String>
) : SerializableModel() {
    companion object : SerializerObject<DomainServices>()
}