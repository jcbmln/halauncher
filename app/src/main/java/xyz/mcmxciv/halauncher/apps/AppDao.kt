package xyz.mcmxciv.halauncher.apps

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppDao {
    @Query("select * from apps order by display_name asc")
    suspend fun getApps(): List<App>

    @Query("select * from apps where activity_name = :activityName")
    suspend fun getApp(activityName: String): App?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: App)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(app: App)

    @Delete
    suspend fun delete(app: App)
}
