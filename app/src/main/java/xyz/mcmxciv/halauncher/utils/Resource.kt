package xyz.mcmxciv.halauncher.utils

sealed class Resource<out T : Any?>(open val data: T?, open val message: String?) {
    object Loading : Resource<Nothing>(null, null)
    data class Error<out T : Any?>(
        override val data: T?, override val message: String
    ) : Resource<T>(data, message)
    data class Success<out T : Any>(override val data: T) : Resource<T>(data, null)
}