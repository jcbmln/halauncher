package xyz.mcmxciv.halauncher.utilities

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.Process
import kotlin.collections.ArrayList

class AppList {
    class AppInfo {
        lateinit var packageName: String
        lateinit var displayName: String
        var icon: Drawable? = null
    }

    companion object {
        fun getAppList(context: Context): ArrayList<AppInfo> {
            val appList = ArrayList<AppInfo>()
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val activityList = launcherApps.getActivityList(null, Process.myUserHandle())
            val packageManager = context.packageManager
            val iconDpi = context.resources.displayMetrics.densityDpi

            for (item in activityList) {
                appList.add(AppInfo().apply {
                    packageName = item.applicationInfo.packageName
                    displayName = packageManager.getApplicationLabel(item.applicationInfo).toString()
                    icon = getIcon(context, item.applicationInfo, iconDpi)
                })
            }

            return appList
        }

        private fun getIcon(context: Context, applicationInfo: ApplicationInfo,
                            iconDpi: Int): Drawable? {
            val resources = context.packageManager
                .getResourcesForApplication(applicationInfo.packageName)
            return resources.getDrawableForDensity(applicationInfo.icon, iconDpi, null)
                ?: resources.getDrawableForDensity(android.R.drawable.sym_def_app_icon,
                    iconDpi, null)
        }
    }
}