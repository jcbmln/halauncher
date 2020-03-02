package xyz.mcmxciv.halauncher.models.apps

import androidx.room.*
import xyz.mcmxciv.halauncher.models.apps.AppInfo

@Dao
interface AppInfoDao {
    @Query("select * from app_info order by display_name asc")
    suspend fun getAllAppInfo(): List<AppInfo>

    @Query("select * from app_info where activity_name = :activityName")
    suspend fun getAppInfo(activityName: String): AppInfo?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appInfo: AppInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(appInfo: AppInfo)

    @Delete
    suspend fun delete(appInfo: AppInfo)
}