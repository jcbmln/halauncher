package xyz.mcmxciv.halauncher.utils

import android.content.pm.LauncherApps
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface ResourceProvider {
    val displayMetrics: DisplayMetrics
    val launcherApps: LauncherApps
    val layoutInflater: LayoutInflater

    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String
    fun getSettingsString(name: String): String?
    fun getDrawable(@DrawableRes resId: Int): Drawable?
    fun getColor(@ColorRes resId: Int): Int
}