package xyz.mcmxciv.halauncher.utils

import android.content.Context
import android.provider.Settings
import androidx.annotation.StringRes

class LauncherResourceProvider(private val context: Context) :
    ResourceProvider {
    override fun getString(@StringRes resId: Int): String = context.getString(resId)

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String =
        context.getString(resId, *formatArgs)

    override fun getSettingsString(name: String): String? =
        Settings.Secure.getString(context.contentResolver, name)
}