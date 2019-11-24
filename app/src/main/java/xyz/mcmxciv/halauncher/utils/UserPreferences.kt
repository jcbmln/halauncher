package xyz.mcmxciv.halauncher.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import xyz.mcmxciv.halauncher.icons.IconShape

object UserPreferences {
    const val HOME_ASSISTANT_KEY = "key_home_assistant_url"
    private const val TRANSPARENT_BACKGROUND_KEY = "key_transparent_background"
    private const val BLUR_BACKGROUND_KEY = "key_blur_background"
    private const val CAN_SET_WALLPAPER_KEY = "key_can_set_wallpaper"
    private const val FIRST_RUN_KEY = "key_first_run"
    private const val ICON_SHAPE_TYPE_KEY = "key_icon_shape_type"

    private var sharedPreferences: SharedPreferences? = null

    var url: String
        get() = getString(HOME_ASSISTANT_KEY)
        set(value) = putString(HOME_ASSISTANT_KEY, value)

    var transparentBackground: Boolean
        get() = getBoolean(TRANSPARENT_BACKGROUND_KEY)
        set(value) = putBoolean(TRANSPARENT_BACKGROUND_KEY, value)

    var blurBackground: Boolean
        get() = getBoolean(BLUR_BACKGROUND_KEY)
        set(value) = putBoolean(BLUR_BACKGROUND_KEY, value)

    var canGetWallpaper: Boolean
        get() = getBoolean(CAN_SET_WALLPAPER_KEY)
        set(value) = putBoolean(CAN_SET_WALLPAPER_KEY, value)

    var isFirstRun: Boolean
        get() = getBoolean(FIRST_RUN_KEY)
        set(value) = putBoolean(FIRST_RUN_KEY, value)

    var iconShapeType: IconShape.ShapeType
        get() {
            val enum = getString(ICON_SHAPE_TYPE_KEY)
            return IconShape.ShapeType.toShapeType(enum)
        }
        set(value) = putString(ICON_SHAPE_TYPE_KEY, value.name)

    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun getString(key: String): String {
        return sharedPreferences?.getString(key, "") ?: ""
    }

    private fun putString(key: String, value: String?) {
        val editor = sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    private fun getBoolean(key: String): Boolean {
        return sharedPreferences?.getBoolean(key, true) ?: true
    }

    private fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }
}