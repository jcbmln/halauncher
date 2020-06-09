package xyz.mcmxciv.halauncher.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    var instanceUrl: String
        get() = sharedPreferences.getString(INSTANCE_URL_KEY, null) ?: PLACEHOLDER_URL
        set(value) = sharedPreferences.edit { putString(INSTANCE_URL_KEY, value) }

    companion object {
        const val INSTANCE_URL_KEY = "instance_url"
        const val PLACEHOLDER_URL = "http://localhost:8123"
    }
}
