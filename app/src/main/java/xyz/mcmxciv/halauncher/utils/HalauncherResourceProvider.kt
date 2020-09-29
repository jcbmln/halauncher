package xyz.mcmxciv.halauncher.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

class HalauncherResourceProvider(private val context: Context) : ResourceProvider {
    override val resources: Resources
        get() = context.resources

    override val displayMetrics: DisplayMetrics
        get() = context.resources.displayMetrics

    override val layoutInflater: LayoutInflater
        get() = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getString(resId: Int): String =
        context.getString(resId)

    override fun getSettingsString(name: String): String? =
        Settings.Secure.getString(context.contentResolver, name)

    override fun getDrawable(@DrawableRes resId: Int): Drawable? =
        ContextCompat.getDrawable(context, resId)

    override fun getColor(resId: Int): Int =
        context.getColor(resId)
}
