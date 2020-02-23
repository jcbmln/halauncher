package xyz.mcmxciv.halauncher.data

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.mcmxciv.halauncher.models.ActivityInfo
import xyz.mcmxciv.halauncher.models.ActivityInfoDao

@Database(entities = [ActivityInfo::class], version = 1, exportSchema = true)
abstract class ActivityInfoDatabase : RoomDatabase() {
    abstract fun acctivityInfoDao(): ActivityInfoDao
}