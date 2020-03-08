package xyz.mcmxciv.halauncher.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.data.models.Config
import xyz.mcmxciv.halauncher.data.models.DeviceIntegration
import xyz.mcmxciv.halauncher.data.models.DeviceRegistration
import xyz.mcmxciv.halauncher.data.models.toJson
import xyz.mcmxciv.halauncher.models.Session
import javax.inject.Inject

class LocalStorageRepository @Inject constructor(
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

    var config: Config?
        get() = getString(CONFIG_KEY)?.let { Config.fromJson(it) }
        set(value) = putString(CONFIG_KEY, value?.toJson())

    var sensorUpdateInterval: Long
        get() = getLong(SENSOR_UPDATE_INTERVAL_KEY, 15)
        set(value) = putLong(SENSOR_UPDATE_INTERVAL_KEY, value)

    val hasHomeAssistantInstance: Boolean
        get() = baseUrl != PLACEHOLDER_URL

    val isAuthenticated: Boolean
        get() = session != null

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

    private fun getLong(key: String, defaultValue: Long): Long =
        sharedPreferences.getLong(key, defaultValue)

    private fun putLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    companion object {
        private const val HOME_ASSISTANT_URL_KEY = "home_assistant_url"
        private const val SESSION_KEY = "session"
        private const val DEVICE_REGISTRATION_KEY = "device_registration"
        private const val DEVICE_INTEGRATION_KEY = "device_integration"
        private const val CONFIG_KEY = "config"
        private const val SENSOR_UPDATE_INTERVAL_KEY = "sensor_udpate_interval"

        const val PLACEHOLDER_URL = "http://localhost:8123/"
    }
}
