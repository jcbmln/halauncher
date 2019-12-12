package xyz.mcmxciv.halauncher.di.modules

import android.content.Context
import android.net.nsd.NsdManager
import dagger.Module
import dagger.Provides
import xyz.mcmxciv.halauncher.SystemServiceInstance
import xyz.mcmxciv.halauncher.di.scopes.FragmentScope

@Module
class DiscoveryModule {
    @FragmentScope
    @Provides
    fun provideNsdManager(context: Context): NsdManager {
        return SystemServiceInstance(NsdManager::class.java).getInstance(context) as NsdManager
    }
}