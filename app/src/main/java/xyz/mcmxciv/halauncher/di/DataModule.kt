package xyz.mcmxciv.halauncher.di

import android.content.Context
import android.net.nsd.NsdManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import xyz.mcmxciv.halauncher.apps.AppDao
import xyz.mcmxciv.halauncher.apps.AppDatabase
import xyz.mcmxciv.halauncher.authentication.AuthenticationApi
import xyz.mcmxciv.halauncher.integration.IntegrationApi
import xyz.mcmxciv.halauncher.integration.SecureIntegrationApi

@Module
@InstallIn(ApplicationComponent::class)
object DataModule {
    @Provides
    fun authenticationApi(@Api retrofit: Retrofit): AuthenticationApi =
        retrofit.create(AuthenticationApi::class.java)

    @Provides
    fun integrationApi(@Api retrofit: Retrofit): IntegrationApi =
        retrofit.create(IntegrationApi::class.java)

    @Provides
    fun secureIntegrationApi(@SecureApi retrofit: Retrofit): SecureIntegrationApi =
        retrofit.create(SecureIntegrationApi::class.java)

    @Provides
    fun nsdManager(@ApplicationContext context: Context): NsdManager =
        context.getSystemService(NsdManager::class.java) as NsdManager

    @Provides
    fun appDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun appDao(database: AppDatabase): AppDao = database.appDao()
}
