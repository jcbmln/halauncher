package xyz.mcmxciv.halauncher.data.repositories

import xyz.mcmxciv.halauncher.models.apps.AppInfo
import xyz.mcmxciv.halauncher.models.apps.AppInfoDao
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appInfoDao: AppInfoDao
) {
    suspend fun getApps(): List<AppInfo> =
        appInfoDao.getAllAppInfo()

    suspend fun getApp(activityName: String): AppInfo? =
        appInfoDao.getAppInfo(activityName)

    suspend fun addAppInfo(appInfo: AppInfo) {
        appInfoDao.insert(appInfo)
    }

    suspend fun updateAppInfo(appInfo: AppInfo) {
        appInfoDao.update(appInfo)
    }

    suspend fun removeAppInfo(appInfo: AppInfo) {
        appInfoDao.delete(appInfo)
    }
}