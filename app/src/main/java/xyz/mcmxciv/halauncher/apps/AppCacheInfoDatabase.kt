package xyz.mcmxciv.halauncher.apps

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xyz.mcmxciv.halauncher.utils.Converters

@Database(entities = [AppCacheInfo::class], version = 4, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppCacheInfoDatabase : RoomDatabase() {
    abstract fun appDao(): AppCacheInfoDao
}
