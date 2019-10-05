package xyz.mcmxciv.halauncher.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceManager

object UserSettings {
    private const val userSettingsKey = "UserSettings"
    private const val urlKey: String = "key_home_assistant_url"
    private const val backgroundTransparencyKey = "key_transparent_background"
    private const val blurBackgroundKey = "key_blur_background"
    private const val canSetWallpaperKey = "key_can_set_wallpaper"

    private var sharedPreferences: SharedPreferences? = null

    var url: String?
        get() = sharedPreferences?.getString(urlKey, null)
        set(value) {
            putString(urlKey, value)
        }

    var transparentBackground: Boolean
        get() = sharedPreferences?.getBoolean(backgroundTransparencyKey, false) ?: false
        set(value) {
            putBoolean(backgroundTransparencyKey, value)
        }

    var blurBackground: Boolean
        get() = sharedPreferences?.getBoolean(blurBackgroundKey, false) ?: false
        set(value) {
            putBoolean(blurBackgroundKey, value)
        }

    var canGetWallpaper: Boolean
        get() = sharedPreferences?.getBoolean(canSetWallpaperKey, false) ?: false
        set(value) {
            putBoolean(canSetWallpaperKey, value)
        }

    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun putString(key: String, value: String?) {
        val editor = sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    private fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }
}