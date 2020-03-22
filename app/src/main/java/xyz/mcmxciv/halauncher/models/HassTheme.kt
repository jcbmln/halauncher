package xyz.mcmxciv.halauncher.models

import android.content.Context
import android.graphics.Color
import org.json.JSONException
import org.json.JSONObject
import xyz.mcmxciv.halauncher.R
import java.lang.IllegalArgumentException

data class HassTheme(
    val name: String,
    val version: Int,
    val primaryColor: Int,
    val accentColor: Int,
    val primaryTextColor: Int
) {
    companion object {
        fun parse(theme: String, context: Context): HassTheme {
            val json = try {
                JSONObject(theme)
            } catch (ex: JSONException) {
                JSONObject()
            }

            val name = if (json.has("name")) json.getString("name") else ""
            val version = if (json.has("version")) json.getInt("version") else -1
            val styles = if (json.has("styles"))
                    json.getJSONObject("styles")
                else JSONObject()

            val primaryColor = if (styles.has("primary-color"))
                parseColor(
                    styles.getString("primary-color"),
                    context.getColor(R.color.colorPrimary)
                )
            else context.getColor(R.color.colorPrimary)

            val accentColor = if (styles.has("accent-color"))
                parseColor(
                    styles.getString("accent-color"),
                    context.getColor(R.color.colorAccent)
                )
            else context.getColor(R.color.colorAccent)

            val primaryTextColor = if (styles.has("primary-text-color"))
                parseColor(
                    styles.getString("primary-text-color"),
                    context.getColor(R.color.colorForeground)
                )
            else context.getColor(R.color.colorForeground)

            return HassTheme(
                name,
                version,
                primaryColor,
                accentColor,
                primaryTextColor
            )
        }

        private fun parseColor(color: String, defaultColor: Int): Int {
            return try {
                Color.parseColor(color)
            } catch (ex: IllegalArgumentException) {
                defaultColor
            }
        }
    }
}