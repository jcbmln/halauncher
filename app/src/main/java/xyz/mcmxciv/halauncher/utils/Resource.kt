package xyz.mcmxciv.halauncher.utils

data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T): Resource<T> =
            Resource(Status.SUCCESS, data)

        fun <T> error(message: String? = null, data: T? = null) =
            Resource(Status.ERRROR, data, message)
    }

    enum class Status {
        SUCCESS,
        ERRROR
    }
}
