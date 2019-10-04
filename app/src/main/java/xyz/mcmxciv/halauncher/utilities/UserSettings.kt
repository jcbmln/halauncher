package xyz.mcmxciv.halauncher.utilities

import android.content.Context
import android.content.SharedPreferences

object UserSettings {
    private const val userSettingsKey = "UserSettings"
    private const val urlKey: String = "hass_url"

    private var sharedPreferences: SharedPreferences? = null

    var url: String?
        get() = sharedPreferences?.getString(urlKey, null)
        set(value) {
            val editor = sharedPreferences!!.edit()
            editor.putString(urlKey, value)
            editor.apply()
        }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(userSettingsKey, 0)
    }
}