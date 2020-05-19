package xyz.mcmxciv.halauncher.data.models

sealed class IntegrationResult {
    data class Success<T : Any?>(val data: T? = null) : IntegrationResult()
    data class Error<T : Any?>(val data: T? = null, val message: String) : IntegrationResult()
}
