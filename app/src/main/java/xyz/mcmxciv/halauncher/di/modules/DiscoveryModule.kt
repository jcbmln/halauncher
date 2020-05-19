package xyz.mcmxciv.halauncher.di.modules

import android.content.Context
import android.net.nsd.NsdManager
import dagger.Module
import dagger.Provides

@Module
class DiscoveryModule {
    @Provides
    fun provideNsdManager(context: Context): NsdManager {
        return context.getSystemService(NsdManager::class.java) as NsdManager
    }
}
