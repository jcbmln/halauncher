package xyz.mcmxciv.halauncher.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.device.DeviceProfile
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
    var connectionUrl: String
        get() = sharedPreferences.getString(CONNECTION_URL_KEY, null) ?: PLACEHOLDER_URL
        set(value) = sharedPreferences.edit { putString(CONNECTION_URL_KEY, value) }

    var storedAppDrawerColumns: Int?
        get() {
            val columns = sharedPreferences.getString(APP_DRAWER_COLUMNS_KEY, null)
            return columns?.toIntOrNull()
        }
        set(value) = sharedPreferences.edit { putString(APP_DRAWER_COLUMNS_KEY, value.toString()) }

    var theme: HassTheme
        get() {
            val themeString = sharedPreferences.getString(THEME_KEY, null)
            val theme = themeString?.let {
                Serializer.deserialize<HassTheme>(it)
            } ?: HassTheme.createDefaultTheme(resourceProvider)

            if (theme.resourceProvider == null) theme.resourceProvider = resourceProvider
            return theme
        }
        set(value) = sharedPreferences.edit { putString(THEME_KEY, Serializer.serialize(value)) }

    companion object {
        const val CONNECTION_URL_KEY = "connection_url"
        const val APP_DRAWER_COLUMNS_KEY = "app_drawer_columns"
        const val THEME_KEY = "theme"
        const val PLACEHOLDER_URL = "http://localhost:8123"
    }
}
