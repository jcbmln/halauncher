package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import javax.inject.Inject

class AppUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    private val shortcutComparator = Comparator<ShortcutInfo> { a, b ->
        when {
            a.isDeclaredInManifest && !b.isDeclaredInManifest -> -1
            !a.isDeclaredInManifest && b.isDeclaredInManifest -> 1
            else -> a.rank.compareTo(b.rank)
        }
    }

    suspend fun getAppDrawerItems(): List<AppDrawerItem> {
        val launcherActivityInfo = appRepository.getLauncherActivityInfo()
        val cachedApps = appRepository.getApps()
        val appDrawerItems = cachedApps.filterNot { app -> app.isHidden }.mapNotNull { app ->
            val launcherActivity = launcherActivityInfo.singleOrNull { info ->
                info.name == app.activityName
            }

            if (launcherActivity == null) {
                appRepository.removeApp(app)
                return@mapNotNull null
            }

            val packageInfo = appRepository.getPackageInfo(app.packageName)
            if (packageInfo.lastUpdateTime > app.lastUpdate) {
                app.lastUpdate = packageInfo.lastUpdateTime
                app.icon = ByteArray(0)
                appRepository.updateApp(app)
            }

            AppDrawerItem(
                app,
                launcherActivity.componentName,
                createShortcuts(app.packageName, launcherActivity.componentName)
            )
        }
        return listOf()
    }

    suspend fun markActivityHidden(activityName: String) {
        appRepository.getApp(activityName)?.let { app ->
            app.isHidden = true
            appRepository.updateApp(app)
        }
    }

    private fun createShortcuts(packageName: String, componentName: ComponentName): List<Shortcut> {
        return if (appRepository.hasShortcutHostPermission) {
            appRepository
                .queryShortcuts(packageName, componentName)
                .sortedWith(shortcutComparator)
                .take(4)
                .map { s ->
                    Shortcut(s.id, packageName, s.shortLabel!!.toString(), Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888))
                }
        } else listOf()
    }
}
