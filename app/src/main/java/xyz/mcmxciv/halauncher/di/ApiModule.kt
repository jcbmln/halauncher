package xyz.mcmxciv.halauncher.di

import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.api.AuthenticationApi
import xyz.mcmxciv.halauncher.api.IntegrationApi
import xyz.mcmxciv.halauncher.utils.AppPreferences

@Module
class ApiModule {
    @Provides
    @Reusable
    internal fun provideAuthenticationApi(retrofit: Retrofit): AuthenticationApi {
        return retrofit.create(AuthenticationApi::class.java)
    }

    @Provides
    @Reusable
    internal fun provideIntegrationApi(retrofit: Retrofit): IntegrationApi {
        return retrofit.create(IntegrationApi::class.java)
    }

    @Provides
    @Reusable
    internal fun provideRetrofit(): Retrofit {
        val url = AppPreferences.getInstance(LauncherApplication.getAppContext()).url
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}