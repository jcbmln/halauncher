package xyz.mcmxciv.halauncher.repositories

import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile

class ApplicationRepository {
    suspend fun getAppList() : List<AppInfo> {
        val appList = getInstalledApplications()
        appList.sortBy { item -> item.displayName }

        return appList
    }

    private suspend fun getInstalledApplications(): ArrayList<AppInfo> {
        return withContext(Dispatchers.Default) {
            val appList = ArrayList<AppInfo>()
            val context = LauncherApplication.getAppContext()
            val pm = context.packageManager
            val launcherIntent = Intent().apply { addCategory(Intent.CATEGORY_LAUNCHER) }
            val idp = InvariantDeviceProfile.getInstance(context)
            val factory = IconFactory(context, idp.iconBitmapSize)

            pm.getInstalledApplications(0).forEach { appInfo ->
                launcherIntent.`package` = appInfo.packageName
                // only show launch-able apps
                if (pm.queryIntentActivities(launcherIntent, 0).size > 0) {
                    val unbadgedIcon = appInfo.loadUnbadgedIcon(pm)
                    appList.add(AppInfo().apply {
                        packageName = appInfo.packageName
                        displayName = pm.getApplicationLabel(appInfo).toString()
                        icon = factory.createIcon(unbadgedIcon)
                    })
                }
            }

            appList
        }
    }
}