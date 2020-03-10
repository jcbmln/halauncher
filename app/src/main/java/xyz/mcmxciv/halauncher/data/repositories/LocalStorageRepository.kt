package xyz.mcmxciv.halauncher.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.data.models.Config
import xyz.mcmxciv.halauncher.data.models.DeviceIntegration
import xyz.mcmxciv.halauncher.data.models.DeviceRegistration
import xyz.mcmxciv.halauncher.data.models.toJson
import xyz.mcmxciv.halauncher.models.Session
import javax.inject.Inject

class LocalStorageRepository @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    var baseUrl: String
        get() = getString(R.string.pk_homeassistant_url) ?: PLACEHOLDER_URL
        set(value) = putString(R.string.pk_homeassistant_url, value)

    var session: Session?
        get() = getString(R.string.pk_session)?.let { Session.fromJson(it) }
        set(value) = putString(R.string.pk_session, value?.toJson() )

    var deviceRegistration: DeviceRegistration?
        get() = getString(R.string.pk_device_registration)?.let { DeviceRegistration.fromJson(it) }
        set(value) = putString(R.string.pk_device_registration, value?.toJson())

    var deviceIntegration: DeviceIntegration?
        get() = getString(R.string.pk_device_integration)?.let { DeviceIntegration.fromJson(it) }
        set(value) = putString(R.string.pk_device_integration, value?.toJson())

    var deviceName: String
        get() = getString(R.string.pk_device_name)
            ?: Settings.Secure.getString(context.contentResolver, "bluetooth_name")
            ?: Build.MODEL
        set(value) = putString(R.string.pk_device_name, value)

    var config: Config?
        get() = getString(R.string.pk_config)?.let { Config.fromJson(it) }
        set(value) = putString(R.string.pk_config, value?.toJson())

    var sensorUpdateInterval: Int
        get() = getInt(R.string.pk_sensor_update_interval, 15)
        set(value) = putInt(R.string.pk_sensor_update_interval, value)

    val hasHomeAssistantInstance: Boolean
        get() = baseUrl != PLACEHOLDER_URL

    val isAuthenticated: Boolean
        get() = session != null

    private fun getString(@StringRes resId: Int): String? {
        val key = context.getString(resId)
        return sharedPreferences.getString(key, null)
    }

    private fun putString(@StringRes resId: Int, value: String?) {
        val key = context.getString(resId)
        sharedPreferences.edit { putString(key, value) }
    }

    private fun getBoolean(@StringRes resId: Int): Boolean {
        val key = context.getString(resId)
        return sharedPreferences.getBoolean(key, false)
    }

    private fun putBoolean(@StringRes resId: Int, value: Boolean) {
        val key = context.getString(resId)
        return sharedPreferences.edit { putBoolean(key, value) }
    }

    private fun getInt(@StringRes resId: Int, defaultValue: Int): Int {
        val key = context.getString(resId)
        return sharedPreferences.getInt(key, defaultValue)
    }

    private fun putInt(@StringRes resId: Int, value: Int) {
        val key = context.getString(resId)
        sharedPreferences.edit { putInt(key, value) }
    }

    companion object {
        const val PLACEHOLDER_URL = "http://localhost:8123/"
    }
}
