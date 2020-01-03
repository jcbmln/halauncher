package xyz.mcmxciv.halauncher.utils

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ResourceLiveData<T : Any> : MutableLiveData<Resource<T>>(Resource.Loading) {
    private fun postLoading() = postValue(Resource.Loading)

    private fun postError(message: String) {
        val stateData = value?.data
        postValue(Resource.Error(stateData, message))
    }

    fun postSuccess(data: T) {
        postValue(Resource.Success(data, null))
    }

    fun postValue(scope: CoroutineScope, message: String? = null, block: suspend () -> T) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
            postError(message ?: ex.message ?: "Unknown error.")
        }

        postLoading()
        scope.launch(exceptionHandler) { postSuccess(block()) }
    }
}