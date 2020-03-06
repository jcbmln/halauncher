package xyz.mcmxciv.halauncher.data.dao

import androidx.room.*
import xyz.mcmxciv.halauncher.data.models.Shortcut

@Dao
interface ShortcutDao {
    @Query("select * from shortcuts order by display_name asc")
    suspend fun getShortcuts(): List<Shortcut>

    @Query("select * from shortcuts where activity_name = :activityName")
    suspend fun getShortcut(activityName: String): Shortcut?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shortcut: Shortcut)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(shortcut: Shortcut)

    @Delete
    suspend fun delete(shortcut: Shortcut)
}