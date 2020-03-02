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
import xyz.mcmxciv.halauncher.data.interactors.UrlInteractor
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.di.qualifiers.Api
import xyz.mcmxciv.halauncher.di.qualifiers.SecureApi
import xyz.mcmxciv.halauncher.models.apps.AppInfoDao

@Module
class DataModule {
    @Provides
    fun homeAssistantApi(@Api retrofit: Retrofit): HomeAssistantApi =
        retrofit.create(HomeAssistantApi::class.java)

    @Provides
    fun homeAssistantSecureApi(@SecureApi retrofit: Retrofit): HomeAssistantSecureApi =
        retrofit.create(HomeAssistantSecureApi::class.java)

    @Provides
    fun moshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

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

    @Provides
    fun appInfoDatabase(context: Context): AppInfoDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppInfoDatabase::class.java,
            "app_info_database"
        ).build()

    @Provides
    fun appInfoDao(database: AppInfoDatabase): AppInfoDao =
        database.appInfoDao()
}