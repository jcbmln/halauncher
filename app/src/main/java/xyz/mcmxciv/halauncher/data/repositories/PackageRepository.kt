package xyz.mcmxciv.halauncher.data.repositories

import android.content.ComponentName
import android.content.Intent
import android.content.pm.*
import android.content.pm.LauncherApps.ShortcutQuery
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PackageRepository @Inject constructor(
    private val packageManager: PackageManager,
    private val launcherApps: LauncherApps
) {
    val hasShortcutHostPermission: Boolean
        get() = launcherApps.hasShortcutHostPermission()

    fun getLauncherActivityInfo(): List<LauncherActivityInfo> =
        launcherApps.getActivityList(null, Process.myUserHandle())

    fun queryShortcuts(packageName: String, activity: ComponentName): List<ShortcutInfo> {
        val query = ShortcutQuery()
        val flags = ShortcutQuery.FLAG_MATCH_MANIFEST or ShortcutQuery.FLAG_MATCH_DYNAMIC
        query.setQueryFlags(flags)
        query.setPackage(packageName)
        query.setActivity(activity)
        return launcherApps.getShortcuts(query, Process.myUserHandle()) ?: listOf()
    }

    fun getPackageInfo(packageName: String): PackageInfo =
        packageManager.getPackageInfo(packageName, 0)

    fun getDisplayName(launcherActivityInfo: LauncherActivityInfo): String =
        launcherActivityInfo.applicationInfo.loadLabel(packageManager).toString()
}