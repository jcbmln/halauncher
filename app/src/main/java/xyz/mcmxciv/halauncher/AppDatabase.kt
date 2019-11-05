package xyz.mcmxciv.halauncher

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xyz.mcmxciv.halauncher.models.Favorites
import xyz.mcmxciv.halauncher.models.FavoritesDao

@Database(entities = [Favorites::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appBarItemDao(): FavoritesDao
    
    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { instance = it }
            }
        }
    }
}