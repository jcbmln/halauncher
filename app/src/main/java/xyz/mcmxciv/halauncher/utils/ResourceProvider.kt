package xyz.mcmxciv.halauncher.utils

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface ResourceProvider {
    val displayMetrics: DisplayMetrics
    val layoutInflater: LayoutInflater
    val resources: Resources

    fun getString(@StringRes resId: Int): String
    fun getSettingsString(name: String): String?
    fun getDrawable(@DrawableRes resId: Int): Drawable?
    fun getColor(@ColorRes resId: Int): Int
}
