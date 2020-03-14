package xyz.mcmxciv.halauncher.data.interactors

import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.ShortcutInfo
import xyz.mcmxciv.halauncher.data.models.App
import xyz.mcmxciv.halauncher.data.repositories.AppRepository
import xyz.mcmxciv.halauncher.data.repositories.PackageRepository
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.models.apps.ShortcutItem
import xyz.mcmxciv.halauncher.utils.toByteArray
import javax.inject.Inject

class AppsInteractor @Inject constructor(
    private val packageRepository: PackageRepository,
    private val appRepository: AppRepository,
    private val iconFactory: IconFactory
) {
    private val shortcutComparator = Comparator<ShortcutInfo> { a, b ->
        return@Comparator when {
            a.isDeclaredInManifest && !b.isDeclaredInManifest -> -1
            !a.isDeclaredInManifest && b.isDeclaredInManifest -> 1
            else -> a.rank.compareTo(b.rank)
        }
    }

    suspend fun getAppListItems(): List<AppListItem> {
        val launcherActivityInfo = packageRepository.getLauncherActivityInfo()
        val cachedApps = appRepository.getApps()
        val appListItems = cachedApps.mapNotNull { app ->
            val launcherActivity = launcherActivityInfo.singleOrNull { info ->
                info.name == app.activityName
            }

            if (launcherActivity == null) {
                appRepository.removeApp(app)
                return@mapNotNull null
            }

            val packageInfo = packageRepository.getPackageInfo(app.packageName)
            if (packageInfo.lastUpdateTime > app.lastUpdate) {
                app.lastUpdate = packageInfo.lastUpdateTime
                app.icon = iconFactory.getIcon(launcherActivity).toByteArray()
                appRepository.updateApp(app)
            }

            return@mapNotNull AppListItem(
                app,
                launcherActivity.componentName,
                createShortcuts(app.packageName, launcherActivity.componentName)
            )
        }.toMutableList()

        val newAppListItems = launcherActivityInfo.filterNot { info ->
            appListItems.map { a -> a.activityName }.contains(info.name)
        }.map { info ->
            val packageInfo = packageRepository.getPackageInfo(info.applicationInfo.packageName)
            val app = App(
                info.name,
                packageInfo.packageName,
                packageRepository.getDisplayName(info),
                packageInfo.lastUpdateTime,
                (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1,
                false,
                iconFactory.getIcon(info).toByteArray()
            )
            appRepository.addApp(app)
            return@map AppListItem(
                app,
                info.componentName,
                createShortcuts(app.packageName, info.componentName)
            )
        }

        appListItems.addAll(newAppListItems)
        appListItems.sortBy { a -> a.displayName }

        return appListItems
    }

    private fun createShortcuts(
        packageName: String,
        componentName: ComponentName
    ): List<ShortcutItem> {
        return if (packageRepository.hasShortcutHostPermission) {
            val shortcuts = packageRepository
                .queryShortcuts(packageName, componentName)
                .sortedWith(shortcutComparator)
                .take(4)

            shortcuts.mapNotNull { s ->
                iconFactory.getShortcutIcon(s)?.let {
                    ShortcutItem(
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