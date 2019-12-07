package xyz.mcmxciv.halauncher.di

import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import xyz.mcmxciv.halauncher.AppDatabase
import xyz.mcmxciv.halauncher.LauncherApplication

@Module
class DbModule {
    @Provides
    @Reusable
    internal fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(
            LauncherApplication.getAppContext(),
            AppDatabase::class.java,
            "appDb").build()
    }

    @Provides
    @Reusable
    internal fun provideSessionDao(db: AppDatabase) = db.sessionDao()

    @Provides
    @Reusable
    internal fun provideDeviceIntegrationDao(db: AppDatabase) = db.deviceIntegrationDao()
}