package xyz.mcmxciv.halauncher.data

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.mcmxciv.halauncher.data.dao.AppDao
import xyz.mcmxciv.halauncher.models.apps.AppInfo
import xyz.mcmxciv.halauncher.data.dao.AppInfoDao
import xyz.mcmxciv.halauncher.data.dao.ShortcutDao
import xyz.mcmxciv.halauncher.data.models.App
import xyz.mcmxciv.halauncher.data.models.Shortcut

@Database(entities = [App::class, Shortcut::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun shortcutDao(): ShortcutDao
}