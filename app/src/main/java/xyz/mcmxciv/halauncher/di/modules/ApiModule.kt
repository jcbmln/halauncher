package xyz.mcmxciv.halauncher.di.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import xyz.mcmxciv.halauncher.api.HomeAssistantApi
import xyz.mcmxciv.halauncher.di.scopes.FragmentScope
import xyz.mcmxciv.halauncher.utils.AppSettings

@Module
class ApiModule {
    @FragmentScope
    @Provides
    fun provideHomeAssistantApi(retrofit: Retrofit): HomeAssistantApi {
        return retrofit.create(HomeAssistantApi::class.java)
    }

    @FragmentScope
    @Provides
    fun provideRetrofit(appPreferences: AppSettings): Retrofit {
        return Retrofit.Builder()
            .baseUrl(appPreferences.url)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}