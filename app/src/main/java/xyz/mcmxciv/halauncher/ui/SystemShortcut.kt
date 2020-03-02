package xyz.mcmxciv.halauncher.ui

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.view.View
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.models.apps.AppInfo
import xyz.mcmxciv.halauncher.utils.getBounds

abstract class SystemShortcut(private val iconResId: Int, private val labelResId: Int) {
    fun getIcon(context: Context): Drawable =
        context.resources.getDrawable(iconResId, context.theme)

    fun getLabel(context: Context): String =
        context.getString(labelResId)

    abstract fun getOnClickListener(context: Context, appInfo: AppInfo): View.OnClickListener

    companion object {
        class AppInfoShortcut : SystemShortcut(
            R.drawable.ic_info_dark,
            R.string.system_shortcut_app_info
        ) {
            override fun getOnClickListener(context: Context, appInfo: AppInfo): View.OnClickListener =
                View.OnClickListener {  view ->
                    val sourceBounds = view.getBounds()
                    val launcherApps =
                        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

                }
        }
    }
}