package xyz.mcmxciv.halauncher.data.repositories

import android.content.Intent
import android.content.pm.*
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PackageRepository @Inject constructor(
    private val packageManager: PackageManager,
    private val launcherApps: LauncherApps
) {
    fun getLauncherActivityInfo(): List<LauncherActivityInfo> =
        launcherApps.getActivityList(null, Process.myUserHandle())

    suspend fun getResolveInfo(): List<ResolveInfo> {
        return withContext(Dispatchers.Default) {
            val launcherIntent = Intent(Intent.ACTION_MAIN, null)
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

            return@withContext packageManager.queryIntentActivities(launcherIntent, 0)
        }
    }

    fun getShortcutActivityInfo(packageName: String): List<ActivityInfo> {
        val intent = Intent(Intent.ACTION_CREATE_SHORTCUT)
        val resolveInfo = packageManager.queryIntentActivities(intent, 0)
        val result = mutableListOf<ActivityInfo>()
        for (info in resolveInfo) {
            if (info.activityInfo.packageName == packageName) {
                result.add(info.activityInfo)
            }
        }

        return result
    }

    fun getPackageInfo(packageName: String): PackageInfo =
        packageManager.getPackageInfo(packageName, 0)

    fun getDisplayName(resolveInfo: ResolveInfo): String =
        resolveInfo.loadLabel(packageManager).toString()

    fun getDisplayName(launcherActivityInfo: LauncherActivityInfo): String =
        launcherActivityInfo.applicationInfo.loadLabel(packageManager).toString()
}