package xyz.mcmxciv.halauncher.data.interactors

import xyz.mcmxciv.halauncher.data.repositories.ActivityRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.data.repositories.PackageRepository
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.ActivityInfo
import javax.inject.Inject

class ActivityInteractor @Inject constructor(
    private val packageRepository: PackageRepository,
    private val activityRepository: ActivityRepository,
    private val localStorageRepository: LocalStorageRepository,
    private val iconFactory: IconFactory
) {
    suspend fun getLaunchableActivities(): List<ActivityInfo> {
        val resolveInfo = packageRepository.getResolveInfo()
        val activityInfoList = activityRepository.getActivities().toMutableList()

        for (info in resolveInfo) {
            val packageName = info.activityInfo.packageName
            val activityInfo = activityInfoList.singleOrNull { a -> a.packageName == packageName}
            val packageInfo = packageRepository.getPackageInfo(packageName)

            when {
                activityInfo == null -> {
                    val icon = iconFactory.getIcon(info.activityInfo)
                    val newActivityInfo = ActivityInfo(
                        packageName,
                        packageRepository.getDisplayName(info),
                        packageInfo.lastUpdateTime,
                        localStorageRepository.saveBitmap(packageName, icon),
                        icon
                    )
                    activityRepository.addActivityInfo(newActivityInfo)
                    activityInfoList.add(newActivityInfo)
                }
                packageInfo.lastUpdateTime > activityInfo.lastUpdate -> {
                    val icon = iconFactory.getIcon(info.activityInfo)
                    localStorageRepository.saveBitmap(packageName, icon)
                    activityInfo.icon = icon
                    activityInfo.lastUpdate = packageInfo.lastUpdateTime
                }
                else -> {
                    activityInfo.icon = localStorageRepository.getBitmap(packageName)
                        ?: iconFactory.getIcon(info.activityInfo).also {
                            localStorageRepository.saveBitmap(packageName, it)
                        }
                }
            }
        }

        val packageNames = resolveInfo.map { ri -> ri.activityInfo.packageName }
        activityInfoList.removeIf { a -> !packageNames.contains(a.packageName) }
        activityInfoList.sortBy { a -> a.packageName }

        return activityInfoList
    }
}