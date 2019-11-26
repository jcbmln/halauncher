package xyz.mcmxciv.halauncher.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import xyz.mcmxciv.halauncher.icons.IconShape

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    var url: String
        get() = getString(HOME_ASSISTANT_KEY) ?: ""
        set(value) = putString(HOME_ASSISTANT_KEY, value)

    var transparentBackground: Boolean
        get() = getBoolean(TRANSPARENT_BACKGROUND_KEY)
        set(value) = putBoolean(TRANSPARENT_BACKGROUND_KEY, value)

//    var blurBackground: Boolean
//        get() = getBoolean(BLUR_BACKGROUND_KEY)
//        set(value) = putBoolean(BLUR_BACKGROUND_KEY, value)

//    var canGetWallpaper: Boolean
//        get() = getBoolean(CAN_SET_WALLPAPER_KEY)
//        set(value) = putBoolean(CAN_SET_WALLPAPER_KEY, value)

    var setupDone: Boolean
        get() = getBoolean(FIRST_RUN_KEY)
        set(value) = putBoolean(FIRST_RUN_KEY, value)

    var isAuthenticated: Boolean
        get() = getBoolean(AUTHENTICATED_KEY)
        set(value) = putBoolean(AUTHENTICATED_KEY, value)

    var iconShapeType: IconShape.ShapeType
        get() {
            val enum = getString(ICON_SHAPE_TYPE_KEY)
            return if (enum != null)
                IconShape.ShapeType.toShapeType(enum)
            else
                IconShape.ShapeType.Squircle
        }
        set(value) = putString(ICON_SHAPE_TYPE_KEY, value.name)

    var accessToken: String?
        get() = getString(ACCESS_TOKEN_KEY)
        set(value) = putString(ACCESS_TOKEN_KEY, value)

    var expirationTimestamp: Long
        get() = getLong(EXPIRATION_KEY)
        set(value) = putLong(EXPIRATION_KEY, value)

    var refreshToken: String?
        get() = getString(REFRESH_TOKEN_KEY)
        set(value) = putString(REFRESH_TOKEN_KEY, value)

    var tokenType: String?
        get() = getString(TOKEN_TYPE_KEY)
        set(value) = putString(TOKEN_TYPE_KEY, value)

    var cloudhookUrl: String?
        get() = getString(CLOUDHOOK_URL_KEY)
        set(value) = putString(CLOUDHOOK_URL_KEY, value)

    var remoteUiUrl: String?
        get() = getString(REMOTE_UI_URL_KEY)
        set(value) = putString(REMOTE_UI_URL_KEY, value)

    var secret: String?
        get() = getString(SECRET_KEY)
        set(value) = putString(SECRET_KEY, value)

    var webhookId: String?
        get() = getString(WEBHOOK_ID_KEY)
        set(value) = putString(WEBHOOK_ID_KEY, value)

    private fun getString(key: String): String? =
        sharedPreferences.getString(key, null)

    private fun putString(key: String, value: String?) {
        val editor = sharedPreferences.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    private fun getBoolean(key: String): Boolean =
        sharedPreferences.getBoolean(key, false)

    private fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    private fun getLong(key: String): Long =
        sharedPreferences.getLong(key, -1)

    private fun putLong(key: String, value: Long) {
        val editor = sharedPreferences.edit()
        editor?.putLong(key, value)
        editor?.apply()
    }

    companion object : ContextInstance<AppPreferences>(::AppPreferences) {
        private const val HOME_ASSISTANT_KEY = "home_assistant_url"
        private const val TRANSPARENT_BACKGROUND_KEY = "transparent_background"
//        private const val BLUR_BACKGROUND_KEY = "blur_background"
//        private const val CAN_SET_WALLPAPER_KEY = "can_set_wallpaper"
        private const val FIRST_RUN_KEY = "first_run"
        private const val ICON_SHAPE_TYPE_KEY = "icon_shape_type"
        private const val AUTHENTICATED_KEY = "authenticated"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val EXPIRATION_KEY = "expires_in"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val TOKEN_TYPE_KEY = "token_type"
        private const val CLOUDHOOK_URL_KEY = "cloudhook_url"
        private const val REMOTE_UI_URL_KEY = "remote_ui_url"
        private const val SECRET_KEY = "secret"
        private const val WEBHOOK_ID_KEY = "webhook_id"
    }
}