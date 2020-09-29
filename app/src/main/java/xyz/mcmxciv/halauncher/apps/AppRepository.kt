package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.os.Process
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appDao: AppDao,
    private val packageManager: PackageManager,
    private val launcherApps: LauncherApps
) {
    val hasShortcutHostPermission: Boolean
        get() = launcherApps.hasShortcutHostPermission()

    suspend fun getApps(): List<App> =
        appDao.getApps()

    suspend fun getApp(activityName: String): App? =
        appDao.getApp(activityName)

    suspend fun addApp(app: App) {
        appDao.insert(app)
    }

    suspend fun updateApp(app: App) {
        appDao.update(app)
    }

    suspend fun removeApp(app: App) {
        appDao.delete(app)
    }

    fun getLauncherActivityInfo(): List<LauncherActivityInfo> =
        launcherApps.getActivityList(null, Process.myUserHandle())

    fun queryShortcuts(packageName: String, componentName: ComponentName): List<ShortcutInfo> {
        val query = LauncherApps.ShortcutQuery()
        val flags = LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC
        query.setQueryFlags(flags)
        query.setPackage(packageName)
        query.setActivity(componentName)

        return launcherApps.getShortcuts(query, Process.myUserHandle()) ?: listOf()
    }

    fun getPackageInfo(packageName: String): PackageInfo =
        packageManager.getPackageInfo(packageName, 0)

    fun getDisplayName(launcherActivityInfo: LauncherActivityInfo): String =
        launcherActivityInfo.applicationInfo.loadLabel(packageManager).toString()
}
