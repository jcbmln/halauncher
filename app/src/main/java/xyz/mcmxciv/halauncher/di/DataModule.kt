package xyz.mcmxciv.halauncher.di

import android.content.Context
import android.net.nsd.NsdManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import xyz.mcmxciv.halauncher.authentication.AuthenticationApi
import xyz.mcmxciv.halauncher.integration.IntegrationApi
import xyz.mcmxciv.halauncher.integration.SecureIntegrationApi

@Module
class DataModule {
    @ViewScope
    @Provides
    fun authenticationApi(@Api retrofit: Retrofit): AuthenticationApi =
        retrofit.create(AuthenticationApi::class.java)

    @ViewScope
    @Provides
    fun integrationApi(@Api retrofit: Retrofit): IntegrationApi =
        retrofit.create(IntegrationApi::class.java)

    @ViewScope
    @Provides
    fun secureIntegrationApi(@SecureApi retrofit: Retrofit): SecureIntegrationApi =
        retrofit.create(SecureIntegrationApi::class.java)

    @ViewScope
    @Provides
    fun nsdManager(context: Context): NsdManager =
        context.getSystemService(NsdManager::class.java) as NsdManager
}
