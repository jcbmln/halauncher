package xyz.mcmxciv.halauncher.repositories

import android.content.Context
import android.content.Intent
import androidx.core.graphics.drawable.toDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import javax.inject.Inject

class ApplicationRepository @Inject constructor(
    private val context: Context,
    private val iconFactory: IconFactory
) {
    suspend fun getAppList() : List<AppInfo> {
        val appList = getInstalledApplications()
        appList.sortBy { item -> item.displayName }

        return appList
    }

    private suspend fun getInstalledApplications(): ArrayList<AppInfo> {
        return withContext(Dispatchers.Default) {
            val appList = ArrayList<AppInfo>()
            val pm = context.packageManager
            val launcherIntent = Intent().apply { addCategory(Intent.CATEGORY_LAUNCHER) }

            pm.getInstalledApplications(0).forEach { appInfo ->
                launcherIntent.setPackage(appInfo.packageName)

                // only show launch-able apps
                if (pm.queryIntentActivities(launcherIntent, 0).size > 0) {
                    val unbadgedIcon = appInfo.loadUnbadgedIcon(pm)
                    appList.add(AppInfo(
                        appInfo.packageName,
                        pm.getApplicationLabel(appInfo).toString(),
                        iconFactory.createIconBitmap(unbadgedIcon)
                    ))
                }
            }

            return@withContext appList
        }
    }
}