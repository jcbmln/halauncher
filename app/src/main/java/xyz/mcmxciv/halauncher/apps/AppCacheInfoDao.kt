package xyz.mcmxciv.halauncher.apps

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppCacheInfoDao {
    @Query("select * from app_cache_info")
    suspend fun getAppDrawerItems(): List<AppCacheInfo>

    @Query("select * from app_cache_info where activity_name = :activityName")
    suspend fun getAppDrawerItem(activityName: String): AppCacheInfo?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appCacheInfo: AppCacheInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(appCacheInfo: AppCacheInfo)

    @Delete
    suspend fun delete(appCacheInfo: AppCacheInfo)
}
