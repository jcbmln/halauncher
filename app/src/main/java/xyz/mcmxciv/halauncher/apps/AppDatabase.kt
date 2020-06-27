package xyz.mcmxciv.halauncher.apps

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [App::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
