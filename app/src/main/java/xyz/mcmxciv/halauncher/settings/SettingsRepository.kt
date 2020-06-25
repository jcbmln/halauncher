package xyz.mcmxciv.halauncher.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.utils.HassTheme
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import xyz.mcmxciv.halauncher.utils.Serializer
import xyz.mcmxciv.halauncher.utils.deserialize
import xyz.mcmxciv.halauncher.utils.serialize
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val resourceProvider: ResourceProvider
) {
    var instanceUrl: String
        get() = sharedPreferences.getString(INSTANCE_URL_KEY, null) ?: PLACEHOLDER_URL
        set(value) = sharedPreferences.edit { putString(INSTANCE_URL_KEY, value) }

    var theme: HassTheme
        get() {
            val themeString = sharedPreferences.getString(THEME_KEY, null)
            return themeString?.let {
                Serializer.deserialize<HassTheme>(it)
            } ?: HassTheme.createDefaultTheme(resourceProvider)
        }
        set(value) = sharedPreferences.edit { putString(THEME_KEY, Serializer.serialize(value)) }

    companion object {
        const val INSTANCE_URL_KEY = "instance_url"
        const val THEME_KEY = "theme"
        const val PLACEHOLDER_URL = "http://localhost:8123"
    }
}
