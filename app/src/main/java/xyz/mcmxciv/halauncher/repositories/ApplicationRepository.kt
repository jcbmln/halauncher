package xyz.mcmxciv.halauncher.repositories

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.AppInfo
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
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveInfoList = pm.queryIntentActivities(launcherIntent, 0)

            for (resolveInfo in resolveInfoList) {
                appList.add(AppInfo(
                    resolveInfo.activityInfo.name,
                    resolveInfo.loadLabel(pm).toString(),
                    iconFactory.getIcon(resolveInfo.activityInfo)
                ))
            }

//            pm.getInstalledApplications(0).forEach { appInfo ->
//                launcherIntent.setPackage(appInfo.packageName)
//
//                // only show launch-able apps
//                if (pm.queryIntentActivities(launcherIntent, 0).size > 0) {
//                    val unbadgedIcon = appInfo.loadUnbadgedIcon(pm)
//                    appList.add(AppInfo(
//                        appInfo.packageName,
//                        pm.getApplicationLabel(appInfo).toString(),
//                        iconFactory.getIcon(ac)
//                    ))
//                }
//            }

            return@withContext appList
        }
    }
}