package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.ShortcutInfo
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.utils.toByteArray
import javax.inject.Inject

class AppUseCase @Inject constructor(
    private val appRepository: AppRepository,
    private val iconFactory: IconFactory
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
                app.icon = iconFactory.getIcon(launcherActivity).toByteArray()
                appRepository.updateApp(app)
            }

            AppDrawerItem(
                app,
                launcherActivity.componentName,
                createShortcuts(app.packageName, launcherActivity.componentName)
            )
        }.toMutableList()

        val cacheAppActivityNames = cachedApps.map { app -> app.activityName }
        val newAppDrawerItems = launcherActivityInfo.filterNot { info ->
            cacheAppActivityNames.contains(info.name)
        }.map { info ->
            val packageInfo = appRepository.getPackageInfo(info.applicationInfo.packageName)
            val app = App(
                info.name,
                packageInfo.packageName,
                appRepository.getDisplayName(info),
                packageInfo.lastUpdateTime,
                (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1,
                false,
                iconFactory.getIcon(info).toByteArray()
            )
            appRepository.addApp(app)
            AppDrawerItem(
                app,
                info.componentName,
                createShortcuts(app.packageName, info.componentName)
            )
        }

        appDrawerItems.addAll(newAppDrawerItems)
        appDrawerItems.sortBy { a -> a.app.displayName }

        return appDrawerItems
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
                .mapNotNull { s ->
                    iconFactory.getShortcutIcon(s)?.let {
                        Shortcut(
                            s.id,
                            packageName,
                            s.shortLabel!!.toString(),
                            it
                        )
                    }
                }
        } else listOf()
    }
}
