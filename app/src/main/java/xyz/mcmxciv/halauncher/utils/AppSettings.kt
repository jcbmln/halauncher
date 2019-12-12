package xyz.mcmxciv.halauncher.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import xyz.mcmxciv.halauncher.icons.IconShape
import xyz.mcmxciv.halauncher.models.Token
import javax.inject.Inject

class AppSettings @Inject constructor(private val sharedPreferences: SharedPreferences) {
    var url: String
        get() = getString(HOME_ASSISTANT_URL_KEY) ?: PLACEHOLDER_URL
        set(value) = putString(HOME_ASSISTANT_URL_KEY, value)

    var transparentBackground: Boolean
        get() = getBoolean(TRANSPARENT_BACKGROUND_KEY)
        set(value) = putBoolean(TRANSPARENT_BACKGROUND_KEY, value)

    var setupDone: Boolean
        get() = getBoolean(FIRST_RUN_KEY)
        set(value) = putBoolean(FIRST_RUN_KEY, value)

    var iconShapeType: IconShape.ShapeType
        get() {
            val enum = getString(ICON_SHAPE_TYPE_KEY)
            return if (enum != null)
                IconShape.ShapeType.toShapeType(enum)
            else
                IconShape.ShapeType.Squircle
        }
        set(value) = putString(ICON_SHAPE_TYPE_KEY, value.name)

    var token: Token?
        get() = getString(AUTHENTICATION_TOKEN_KEY)?.let { Token.fromJson(it) }
        set(value) = putString(AUTHENTICATION_TOKEN_KEY, value?.let { Token.toJson(it) })

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
        private const val PLACEHOLDER_URL = "http://localhost:8123/"
        private const val TRANSPARENT_BACKGROUND_KEY = "transparent_background"
        private const val FIRST_RUN_KEY = "first_run"
        private const val ICON_SHAPE_TYPE_KEY = "icon_shape_type"
        private const val AUTHENTICATED_KEY = "authenticated"
        private const val AUTHENTICATION_TOKEN_KEY = "authentication_token"
    }
}
