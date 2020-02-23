package xyz.mcmxciv.halauncher.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.models.*

class LocalStorageRepository(private val sharedPreferences: SharedPreferences) {
    var baseUrl: String
        get() = getString(HOME_ASSISTANT_URL_KEY) ?: PLACEHOLDER_URL
        set(value) = putString(HOME_ASSISTANT_URL_KEY, value)

    var session: Session?
        get() = getString(SESSION_KEY)?.let { Model.fromJson(it) }
        set(value) = putString(SESSION_KEY, value?.toJson<Session>() )

    var deviceRegistration: DeviceRegistration?
        get() = getString(DEVICE_REGISTRATION_KEY)?.let { Model.fromJson(it) }
        set(value) = putString(DEVICE_REGISTRATION_KEY, value?.toJson<DeviceRegistration>())

    var deviceIntegration: DeviceIntegration?
        get() = getString(DEVICE_INTEGRATION_KEY)?.let { Model.fromJson(it) }
        set(value) = putString(DEVICE_INTEGRATION_KEY, value?.toJson<DeviceIntegration>())

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

    companion object {
        private const val HOME_ASSISTANT_URL_KEY = "home_assistant_url"
        private const val SESSION_KEY = "session"
        private const val DEVICE_REGISTRATION_KEY = "device_registration"
        private const val DEVICE_INTEGRATION_KEY = "device_integration"

        const val PLACEHOLDER_URL = "http://localhost:8123/"
    }
}
