package xyz.mcmxciv.halauncher.utils

import android.content.Context
import android.content.pm.LauncherApps
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class LauncherResourceProvider(private val context: Context) :
    ResourceProvider {
    override val displayMetrics: DisplayMetrics
        get() = context.resources.displayMetrics

    override val launcherApps: LauncherApps
        get() = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    override val layoutInflater: LayoutInflater
        get() = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getString(@StringRes resId: Int): String = context.getString(resId)

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String =
        context.getString(resId, *formatArgs)

    override fun getSettingsString(name: String): String? =
        Settings.Secure.getString(context.contentResolver, name)

    override fun getDrawable(@DrawableRes resId: Int): Drawable? =
        context.getDrawable(resId)

    override fun getColor(resId: Int): Int =
        context.getColor(resId)
}