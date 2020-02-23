package xyz.mcmxciv.halauncher.di.modules

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

@Module
class DataModule {
    @Provides
    fun homeAssistantApi(@Api retrofit: Retrofit): HomeAssistantApi =
        retrofit.create(HomeAssistantApi::class.java)

    @Provides
    fun homeAssistantSecureApi(@SecureApi retrofit: Retrofit): HomeAssistantSecureApi =
        retrofit.create(HomeAssistantSecureApi::class.java)

    @Provides
    @SecureApi
    fun secureRetrofit(
        localStorageRepository: LocalStorageRepository,
        urlInteractor: UrlInteractor
    ): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(UrlInterceptor(localStorageRepository))
            .addInterceptor(SessionInterceptor(urlInteractor, localStorageRepository))
            .build()

        return Retrofit.Builder()
            .baseUrl(localStorageRepository.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Api
    fun retrofit(localStorageRepository: LocalStorageRepository): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(UrlInterceptor(localStorageRepository))
            .build()

        return Retrofit.Builder()
            .baseUrl(localStorageRepository.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }
}