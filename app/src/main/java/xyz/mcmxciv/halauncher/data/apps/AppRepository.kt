package xyz.mcmxciv.halauncher.data.apps

import xyz.mcmxciv.halauncher.data.models.App
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appDao: AppDao
) {
    suspend fun getApps(): List<App> =
        appDao.getApps()

    suspend fun getApp(activityName: String): App? =
        appDao.getApp(activityName)

    suspend fun addApp(app: App) {
        appDao.insert(app)
    }

    suspend fun updateApp(app: App) {
        appDao.update(app)
    }

    suspend fun removeApp(app: App) {
        appDao.delete(app)
    }
}
