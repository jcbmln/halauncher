package xyz.mcmxciv.halauncher.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import org.json.JSONException
import org.json.JSONObject
import xyz.mcmxciv.halauncher.R

class HassTheme private constructor(
    val primaryTextColor: Int,
    val secondaryTextColor: Int,
    val textPrimaryColor: Int,
    val disabledTextColor: Int,
    val primaryColor: Int,
    val darkPrimaryColor: Int,
    val lightPrimaryColor: Int,
    val accentColor: Int,
    val errorColor: Int,
    val cardBackgroundColor: Int,
    val primaryBackgroundColor: Int,
    val secondaryBackgroundColor: Int
) {
    val inputStateList: ColorStateList

    init {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_focused, android.R.attr.state_pressed),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf()
        )

        val colors = intArrayOf(
            accentColor,
            primaryTextColor,
            accentColor,
            ColorUtils.setAlphaComponent(primaryTextColor, 192),
            primaryTextColor
        )

        inputStateList = ColorStateList(states, colors)
    }

    companion object {
        fun createDefaultTheme(context: Context): HassTheme =
            HassTheme(
                context.getColor(R.color.primary_text_color),
                context.getColor(R.color.secondary_text_color),
                context.getColor(R.color.text_primary_color),
                context.getColor(R.color.disabled_text_color),
                context.getColor(R.color.primary_color),
                context.getColor(R.color.dark_primary_color),
                context.getColor(R.color.light_primary_color),
                context.getColor(R.color.accent_color),
                context.getColor(R.color.error_color),
                context.getColor(R.color.card_background_color),
                context.getColor(R.color.primary_background_color),
                context.getColor(R.color.secondary_background_color)
            )

        fun createFromString(theme: String, context: Context): HassTheme {
            val default = createDefaultTheme(context)
            val json = try {
                JSONObject(theme)
            } catch (ex: JSONException) {
                JSONObject()
            }

            val styles = if (json.has("styles"))
                    json.getJSONObject("styles")
                else JSONObject()

            return HassTheme(
                styles.getColor("primary-text-color", default.primaryTextColor),
                styles.getColor("secondary-text-color", default.secondaryTextColor),
                styles.getColor("text-primary-color", default.textPrimaryColor),
                styles.getColor("disabled-text-color", default.disabledTextColor),
                styles.getColor("primary-color", default.primaryColor),
                styles.getColor("dark-primary-color", default.darkPrimaryColor),
                styles.getColor("light-primary-color", default.lightPrimaryColor),
                styles.getColor("accent-color", default.accentColor),
                styles.getColor("error-color", default.errorColor),
                styles.getColor("card-background-color", default.cardBackgroundColor),
                styles.getColor("primary-background-color", default.primaryBackgroundColor),
                styles.getColor("secondary-background-color", default.secondaryBackgroundColor)
            )
        }

        private fun JSONObject.getColor(key: String, default: Int): Int {
            return if (has(key)) {
                try {
                    Color.parseColor(getString(key))
                } catch (ex: IllegalArgumentException) {
                    default
                }
            } else default
        }
    }
}