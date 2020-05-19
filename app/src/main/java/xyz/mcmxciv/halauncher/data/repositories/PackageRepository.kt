package xyz.mcmxciv.halauncher.data.repositories

import android.content.ComponentName
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.ShortcutQuery
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.os.Process
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
