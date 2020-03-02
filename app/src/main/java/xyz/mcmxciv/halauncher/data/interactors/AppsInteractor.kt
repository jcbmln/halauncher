package xyz.mcmxciv.halauncher.data.interactors

import xyz.mcmxciv.halauncher.data.repositories.AppRepository
import xyz.mcmxciv.halauncher.data.repositories.PackageRepository
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.apps.AppInfo
import xyz.mcmxciv.halauncher.models.apps.ShortcutInfo
import xyz.mcmxciv.halauncher.utils.toByteArray
import javax.inject.Inject

class AppsInteractor @Inject constructor(
    private val packageRepository: PackageRepository,
    private val appRepository: AppRepository,
    private val iconFactory: IconFactory
) {
    suspend fun getLaunchableActivities(): List<AppInfo> {
        val launcherAppInfoList = packageRepository.getLauncherActivityInfo()
        val cachedAppInfoList = appRepository.getApps()
        val appInfoList = mutableListOf<AppInfo>()

        for (info in cachedAppInfoList) {
            val launcherAppInfo = launcherAppInfoList.singleOrNull { lai ->
                lai.name == info.activityName
            }
            val packageInfo = packageRepository.getPackageInfo(info.packageName)

            when {
                launcherAppInfo == null -> appRepository.removeAppInfo(info)
                packageInfo.lastUpdateTime > info.lastUpdate -> {
                    val icon = iconFactory.getIcon(launcherAppInfo)
                    val activityInfo =
                        AppInfo(
                            info.activityName,
                            info.packageName,
                            info.displayName,
                            packageInfo.lastUpdateTime,
                            icon.toByteArray(),
                            launcherAppInfo.componentName,
                            createShortcuts(info.packageName)
                        )
                    appRepository.updateAppInfo(activityInfo)
                    appInfoList.add(activityInfo)
                }
                else -> {
                    info.componentName = launcherAppInfo.componentName
                    appInfoList.add(info)
                }
            }
        }

        for (info in launcherAppInfoList) {
            val appInfo = appInfoList.singleOrNull { a ->
                a.activityName == info.name
            }

            if (appInfo == null) {
                val packageInfo = packageRepository.getPackageInfo(info.applicationInfo.packageName)
                val icon = iconFactory.getIcon(info)
                val newAppInfo = AppInfo(
                    info.name,
                    packageInfo.packageName,
                    packageRepository.getDisplayName(info),
                    packageInfo.lastUpdateTime,
                    icon.toByteArray(),
                    info.componentName,
                    createShortcuts(packageInfo.packageName)
                )

                appRepository.addAppInfo(newAppInfo)
                appInfoList.add(newAppInfo)
            }
        }

        appInfoList.sortBy { a -> a.displayName }

        return appInfoList
    }

    private fun createShortcuts(packageName: String): List<ShortcutInfo> {
        val shortcutActivityInfo = packageRepository.getShortcutActivityInfo(packageName)
        return shortcutActivityInfo.map { s ->
            ShortcutInfo(
                s.name,
                s.packageName,
                iconFactory.getShortcutIcon(s)
            )
        }
    }
}