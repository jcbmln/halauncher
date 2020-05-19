package xyz.mcmxciv.halauncher.models

sealed class Resource<out T>(
    val status: Status,
    open val data: T?,
    open val message: String?
) {
    object Loading : Resource<Nothing>(Status.LOADING, null, null)

    data class Error<out T>(
        override val data: T?,
        override val message: String
    ) : Resource<T>(Status.ERROR, data, message)

    data class Success<out T>(
        override val data: T,
        override val message: String?
    ) : Resource<T>(Status.SUCCESS, data, message)
}
