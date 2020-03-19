package xyz.mcmxciv.halauncher.di.modules

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import xyz.mcmxciv.halauncher.data.*
import xyz.mcmxciv.halauncher.data.api.HomeAssistantApi
import xyz.mcmxciv.halauncher.data.api.HomeAssistantSecureApi
import xyz.mcmxciv.halauncher.data.dao.AppDao
import xyz.mcmxciv.halauncher.data.interactors.UrlInteractor
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.di.qualifiers.Api
import xyz.mcmxciv.halauncher.di.qualifiers.SecureApi
import xyz.mcmxciv.halauncher.data.dao.ShortcutDao
import javax.inject.Singleton

@Module
class DataModule {
    @Singleton
    @Provides
    fun homeAssistantApi(@Api retrofit: Retrofit): HomeAssistantApi =
        retrofit.create(HomeAssistantApi::class.java)

    @Provides
    fun homeAssistantSecureApi(@SecureApi retrofit: Retrofit): HomeAssistantSecureApi =
        retrofit.create(HomeAssistantSecureApi::class.java)

    @Singleton
    @Provides
    fun moshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Singleton
    @Provides
    @SecureApi
    fun secureRetrofit(
        moshi: Moshi,
        localStorageRepository: LocalStorageRepository,
        urlInteractor: UrlInteractor
    ): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(UrlInterceptor(localStorageRepository))
            .addInterceptor(SessionInterceptor(urlInteractor, localStorageRepository))
            .build()

        return Retrofit.Builder()
            .baseUrl(localStorageRepository.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    @Api
    fun retrofit(moshi: Moshi, localStorageRepository: LocalStorageRepository): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(UrlInterceptor(localStorageRepository))
            .build()

        return Retrofit.Builder()
            .baseUrl(localStorageRepository.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun appDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun appDao(database: AppDatabase): AppDao = database.appDao()

    @Singleton
    @Provides
    fun shortcutDao(database: AppDatabase): ShortcutDao = database.shortcutDao()
}