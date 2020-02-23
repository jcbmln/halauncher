package xyz.mcmxciv.halauncher.data.repositories

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PackageRepository @Inject constructor(
    private val packageManager: PackageManager
) {
    suspend fun getResolveInfo(): List<ResolveInfo> {
        return withContext(Dispatchers.Default) {
            val launcherIntent = Intent(Intent.ACTION_MAIN, null)
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

            return@withContext packageManager.queryIntentActivities(launcherIntent, 0)
        }
    }

    fun getPackageInfo(packageName: String): PackageInfo =
        packageManager.getPackageInfo(packageName, 0)

    fun getDisplayName(resolveInfo: ResolveInfo): String =
        resolveInfo.loadLabel(packageManager).toString()
}