package xyz.mcmxciv.halauncher.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.mcmxciv.halauncher.models.*
import java.io.*
import java.lang.Exception
import javax.inject.Inject

class LocalStorageRepository @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    var baseUrl: String
        get() = getString(HOME_ASSISTANT_URL_KEY) ?: PLACEHOLDER_URL
        set(value) = putString(HOME_ASSISTANT_URL_KEY, value)

    var session: Session?
        get() = getString(SESSION_KEY)?.let { Session.fromJson(it) }
        set(value) = putString(SESSION_KEY, value?.toJson() )

    var deviceRegistration: DeviceRegistration?
        get() = getString(DEVICE_REGISTRATION_KEY)?.let { DeviceRegistration.fromJson(it) }
        set(value) = putString(DEVICE_REGISTRATION_KEY, value?.toJson())

    var deviceIntegration: DeviceIntegration?
        get() = getString(DEVICE_INTEGRATION_KEY)?.let { DeviceIntegration.fromJson(it) }
        set(value) = putString(DEVICE_INTEGRATION_KEY, value?.toJson())

    val hasHomeAssistantInstance: Boolean
        get() = baseUrl != PLACEHOLDER_URL

    val isAuthenticated: Boolean
        get() = session != null

    suspend fun saveBitmap(name: String, bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            val directory = context.getDir("imageDir", Context.MODE_PRIVATE)
            val fileName = "$name.jpg"
            val file = File(directory, fileName)
            file.createNewFile()

            FileOutputStream(file).use { output ->
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                } catch (ex: Exception) {
                    Timber.e(ex)
                }

                output.flush()
            }

            return@withContext fileName
        }
    }

    suspend fun getBitmap(fileName: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val directory = context.getDir("imageDir", Context.MODE_PRIVATE)
            val file = File(directory, fileName)

            return@withContext FileInputStream(file).use { input ->
                try {
                    return@use BitmapFactory.decodeStream(input)
                } catch (ex: FileNotFoundException) {
                    Timber.e(ex)
                    return@use null
                }
            }
        }
    }

    private fun getString(key: String): String? =
        sharedPreferences.getString(key, null)

    private fun putString(key: String, value: String?) {
        sharedPreferences.edit { putString(key, value) }
    }

    private fun getBoolean(key: String): Boolean =
        sharedPreferences.getBoolean(key, false)

    private fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    companion object {
        private const val HOME_ASSISTANT_URL_KEY = "home_assistant_url"
        private const val SESSION_KEY = "session"
        private const val DEVICE_REGISTRATION_KEY = "device_registration"
        private const val DEVICE_INTEGRATION_KEY = "device_integration"

        const val PLACEHOLDER_URL = "http://localhost:8123/"
    }
}
