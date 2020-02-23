package xyz.mcmxciv.halauncher.data.interactors

import xyz.mcmxciv.halauncher.data.repositories.ActivityRepository
import xyz.mcmxciv.halauncher.models.ActivityInfo
import javax.inject.Inject

class ActivityInteractor @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    suspend fun getLaunchableActivities(): List<ActivityInfo> =
        activityRepository.getLaunchableActivities()
}