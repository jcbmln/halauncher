package xyz.mcmxciv.halauncher.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object UserPreferences {
    const val HOME_ASSISTANT_KEY = "key_home_assistant_url"
    private const val TRANSPARENT_BACKGROUND_KEY = "key_transparent_background"
    private const val BLUR_BACKGROUND_KEY = "key_blur_background"
    private const val CAN_SET_WALLPAPER_KEY = "key_can_set_wallpaper"

    private var sharedPreferences: SharedPreferences? = null

    var url: String?
        get() = sharedPreferences?.getString(HOME_ASSISTANT_KEY, null)
        set(value) {
            putString(HOME_ASSISTANT_KEY, value)
        }

    var transparentBackground: Boolean
        get() = sharedPreferences?.getBoolean(TRANSPARENT_BACKGROUND_KEY, false) ?: false
        set(value) {
            putBoolean(TRANSPARENT_BACKGROUND_KEY, value)
        }

    var blurBackground: Boolean
        get() = sharedPreferences?.getBoolean(BLUR_BACKGROUND_KEY, false) ?: false
        set(value) {
            putBoolean(BLUR_BACKGROUND_KEY, value)
        }

    var canGetWallpaper: Boolean
        get() = sharedPreferences?.getBoolean(CAN_SET_WALLPAPER_KEY, false) ?: false
        set(value) {
            putBoolean(CAN_SET_WALLPAPER_KEY, value)
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