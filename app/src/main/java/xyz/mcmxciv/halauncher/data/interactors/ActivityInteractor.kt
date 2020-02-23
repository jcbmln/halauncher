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
            val activityName = info.activityInfo.name
            val activityInfo = activityInfoList.singleOrNull { a -> a.activityName == activityName}
            val packageInfo = packageRepository.getPackageInfo(info.activityInfo.packageName)

            when {
                activityInfo == null -> {
                    val icon = iconFactory.getIcon(info.activityInfo)
                    val newActivityInfo = ActivityInfo(
                        activityName,
                        packageRepository.getDisplayName(info),
                        packageInfo.lastUpdateTime,
                        localStorageRepository.saveBitmap(activityName, icon),
                        icon
                    )
                    activityRepository.addActivityInfo(newActivityInfo)
                    activityInfoList.add(newActivityInfo)
                }
                packageInfo.lastUpdateTime > activityInfo.lastUpdate -> {
                    val icon = iconFactory.getIcon(info.activityInfo)
                    localStorageRepository.saveBitmap(activityName, icon)
                    activityInfo.icon = icon
                    activityInfo.lastUpdate = packageInfo.lastUpdateTime
                }
                else -> {
                    val fileName = activityInfo.iconFileName
                    activityInfo.icon = localStorageRepository.getBitmap(fileName)
                        ?: iconFactory.getIcon(info.activityInfo).also {
                            localStorageRepository.saveBitmap(activityName, it)
                        }
                }
            }
        }

        val packageNames = resolveInfo.map { ri -> ri.activityInfo.name }
        activityInfoList.removeIf { a -> !packageNames.contains(a.activityName) }
        activityInfoList.sortBy { a -> a.displayName }

        return activityInfoList
    }
}