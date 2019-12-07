package xyz.mcmxciv.halauncher

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.mcmxciv.halauncher.dao.DeviceIntegrationDao
import xyz.mcmxciv.halauncher.dao.SessionDao
import xyz.mcmxciv.halauncher.models.DeviceIntegration
import xyz.mcmxciv.halauncher.models.Session

@Database(entities = [Session::class, DeviceIntegration::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun deviceIntegrationDao(): DeviceIntegrationDao
}