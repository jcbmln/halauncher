package xyz.mcmxciv.halauncher.di.modules

import android.content.Context
import android.net.nsd.NsdManager
import dagger.Module
import dagger.Provides
import xyz.mcmxciv.halauncher.di.scopes.ViewScope

@Module
class DiscoveryModule {
    @Provides
    fun provideNsdManager(context: Context): NsdManager {
        return context.getSystemService(NsdManager::class.java) as NsdManager
    }
}