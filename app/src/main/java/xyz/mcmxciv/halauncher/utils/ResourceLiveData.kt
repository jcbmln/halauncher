package xyz.mcmxciv.halauncher.utils

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ResourceLiveData<T : Any> : MutableLiveData<Resource<T>>() {
    fun postLoading() = postValue(Resource.Loading)

    fun postError(message: String) {
        val resourceData = value?.data
        postValue(Resource.Error(resourceData, message))
    }

    private fun postError(error: Resource.Error<T>) = postValue(error)

    fun postSuccess(data: T) = postValue(Resource.Success(data))

    fun postValue(scope: CoroutineScope, message: String? = null, block: suspend () -> T) =
        postValue(scope, null, message, block)

    fun postValue(scope: CoroutineScope, errorValue: Resource.Error<T>, block: suspend () -> T) =
        postValue(scope, errorValue, null, block)

    private fun postValue(
        scope: CoroutineScope,
        errorValue: Resource.Error<T>?,
        message: String?,
        block: suspend () -> T
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)

            if (errorValue != null)
                postError(errorValue)
            else
                postError(message ?: ex.message ?: "Unknown error.")
        }

        postLoading()
        scope.launch(exceptionHandler) { postSuccess(block()) }
    }
}