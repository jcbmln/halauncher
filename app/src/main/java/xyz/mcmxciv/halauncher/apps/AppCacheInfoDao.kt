package xyz.mcmxciv.halauncher.apps

import androidx.room.*

@Dao
interface AppCacheInfoDao {
    @Query("select * from app_cache_info")
    suspend fun get(): List<AppCacheInfo>

    @Query("select * from app_cache_info where activity_name = :activityName")
    suspend fun get(activityName: String): AppCacheInfo?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appCacheInfo: AppCacheInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(appCacheInfo: AppCacheInfo)

    @Delete
    suspend fun delete(appCacheInfo: AppCacheInfo)
}
