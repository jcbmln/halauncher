package xyz.mcmxciv.halauncher.data.cache

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.data.models.Config
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.Session
import xyz.mcmxciv.halauncher.domain.models.WebhookInfo
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class PreferencesLocalCache @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val sharedPreferences: SharedPreferences
) : LocalCache {
    override var instanceUrl: String
        get() = getString(R.string.pk_homeassistant_url) ?: PLACEHOLDER_URL
        set(value) = putString(R.string.pk_homeassistant_url, value)

    override var session: Session?
        get() = getString(R.string.pk_session)?.let { deserialize(it) }
        set(value) = putString(R.string.pk_session, serialize(value))

    override var deviceInfo: DeviceInfo?
        get() = getString(R.string.pk_device_registration)?.let { deserialize(it) }
        set(value) = putString(R.string.pk_device_registration, serialize(value))

    override var webhookInfo: WebhookInfo?
        get() = getString(R.string.pk_webhook_info)?.let { deserialize(it) }
        set(value) = putString(R.string.pk_webhook_info, serialize(value))

    override var deviceName: String
        get() = getString(R.string.pk_device_name)
            ?: resourceProvider.getSettingsString("bluetooth_name")
            ?: Build.MODEL
        set(value) = putString(R.string.pk_device_name, value)

    var config: Config?
        get() = getString(R.string.pk_config)?.let { deserialize(it) }
        set(value) = putString(R.string.pk_config, serialize(value))

    override var sensorIds: Set<String>
        get() = getStringSet(R.string.pk_sensor_ids)
        set(value) = putStringSet(R.string.pk_sensor_ids, value)

    override var sensorUpdateInterval: Int
        get() = getInt(R.string.pk_sensor_update_interval, 15)
        set(value) = putInt(R.string.pk_sensor_update_interval, value)

    override val instanceUrlSet: Boolean
        get() = instanceUrl != PLACEHOLDER_URL

    override val isAuthenticated: Boolean
        get() = session != null

    override var theme: HassTheme?
        get() = getString(R.string.pk_theme)?.let { deserialize(it) }
        set(value) = putString(R.string.pk_theme, serialize(value))

    private fun getString(@StringRes resId: Int): String? {
        val key = resourceProvider.getString(resId)
        return sharedPreferences.getString(key, null)
    }

    private fun putString(@StringRes resId: Int, value: String?) {
        val key = resourceProvider.getString(resId)
        sharedPreferences.edit { putString(key, value) }
    }

    private fun getStringSet(@StringRes resId: Int): Set<String> {
        val key = resourceProvider.getString(resId)
        return sharedPreferences.getStringSet(key, setOf())!!
    }

    private fun putStringSet(@StringRes resId: Int, value: Set<String>) {
        val key = resourceProvider.getString(resId)
        sharedPreferences.edit { putStringSet(key, value) }
    }

    private fun getBoolean(@StringRes resId: Int): Boolean {
        val key = resourceProvider.getString(resId)
        return sharedPreferences.getBoolean(key, false)
    }

    private fun putBoolean(@StringRes resId: Int, value: Boolean) {
        val key = resourceProvider.getString(resId)
        return sharedPreferences.edit { putBoolean(key, value) }
    }

    private fun getInt(@StringRes resId: Int, defaultValue: Int): Int {
        val key = resourceProvider.getString(resId)
        return sharedPreferences.getInt(key, defaultValue)
    }

    private fun putInt(@StringRes resId: Int, value: Int) {
        val key = resourceProvider.getString(resId)
        sharedPreferences.edit { putInt(key, value) }
    }

    companion object {
        const val PLACEHOLDER_URL = "http://localhost:8123"
    }
}
