package xyz.mcmxciv.halauncher.di.modules

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import xyz.mcmxciv.halauncher.api.*
import xyz.mcmxciv.halauncher.di.Api
import xyz.mcmxciv.halauncher.di.SecureApi
import xyz.mcmxciv.halauncher.di.scopes.FragmentScope
import xyz.mcmxciv.halauncher.utils.AppSettings

@Module
class ApiModule {
    @FragmentScope
    @Provides
    fun provideHomeAssistantApi(
        @Api retrofit: Retrofit,
        holder: HomeAssistantApiHolder
    ): HomeAssistantApi {
        val service = retrofit.create(HomeAssistantApi::class.java)
        holder.homeAssistantApi = service
        return service
    }

    @FragmentScope
    @Provides
    fun provideHomeAssistantSecureApi(@SecureApi retrofit: Retrofit): HomeAssistantSecureApi =
        retrofit.create(HomeAssistantSecureApi::class.java)

    @FragmentScope
    @Provides
    fun provideHomeAssistantApiHolder(): HomeAssistantApiHolder {
        return HomeAssistantApiHolder()
    }

    @FragmentScope
    @Provides
    @SecureApi
    fun provideSecureRetrofit(appSettings: AppSettings, holder: HomeAssistantApiHolder): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(appSettings))
            .authenticator(TokenAuthenticator(appSettings, holder))
            .build()

        return Retrofit.Builder()
            .baseUrl(appSettings.url)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    @FragmentScope
    @Provides
    @Api
    fun provideRetrofit(appPreferences: AppSettings): Retrofit {
        return Retrofit.Builder()
            .baseUrl(appPreferences.url)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}