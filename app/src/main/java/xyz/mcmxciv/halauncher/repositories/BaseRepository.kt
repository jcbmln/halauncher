package xyz.mcmxciv.halauncher.repositories

import android.util.Log
import retrofit2.Response
import xyz.mcmxciv.halauncher.utils.Result
import java.io.IOException

open class BaseRepository {
    suspend fun <T: Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {
        val result: Result<T> = safeApiResult(call,errorMessage)
        var data: T? = null

        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error -> {
                Log.d(TAG, errorMessage)
                Log.d(TAG, result.exception.toString())
            }
        }

        return data
    }

    private suspend fun <T: Any> safeApiResult(
        call: suspend () -> Response<T>,
        errorMessage: String
    ) : Result<T> {
        val response = call.invoke()
        if (response.isSuccessful) return Result.Success(response.body()!!)

        return Result.Error(IOException("Error occurred getting safe api result: $errorMessage"))
    }

    companion object {
        private const val TAG = "BaseRepository"
    }
}