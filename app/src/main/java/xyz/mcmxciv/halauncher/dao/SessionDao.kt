package xyz.mcmxciv.halauncher.dao

import androidx.room.*
import xyz.mcmxciv.halauncher.models.Session

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions LIMIT 1")
    suspend fun getSession(): Session?

    @Insert
    suspend fun insertSession(session: Session)

    @Update
    suspend fun updateSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)
}