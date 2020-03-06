package xyz.mcmxciv.halauncher.data

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.mcmxciv.halauncher.models.apps.AppInfo
import xyz.mcmxciv.halauncher.data.dao.AppInfoDao

@Database(entities = [AppInfo::class], version = 1, exportSchema = true)
abstract class AppInfoDatabase : RoomDatabase() {
    abstract fun appInfoDao(): AppInfoDao
}