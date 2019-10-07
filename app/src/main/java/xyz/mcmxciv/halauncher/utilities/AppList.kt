package xyz.mcmxciv.halauncher.utilities

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.os.Process
import android.util.Log

class AppList {
    private val tag = "AppList"
    private val defaultAppNumber = 42

    val data = ArrayList<ApplicationInfo>()
    val added = ArrayList<ApplicationInfo>()
    val removed = ArrayList<ApplicationInfo>()
    val modified = ArrayList<ApplicationInfo>()

    companion object {
        fun getAppList(context: Context) {
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val activityList = launcherApps.getActivityList(null, Process.myUserHandle())

            for (item in activityList) {
                Log.d("blah", item.name)
            }
        }
    }
}