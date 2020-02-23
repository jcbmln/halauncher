package xyz.mcmxciv.halauncher.models

import androidx.room.*

@Dao
interface ActivityInfoDao {
    @Query("select * from activity_info order by display_name asc")
    suspend fun getActivities(): List<ActivityInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(activityInfo: ActivityInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(activityInfo: ActivityInfo)

    @Delete
    suspend fun delete(activityInfo: ActivityInfo)
}