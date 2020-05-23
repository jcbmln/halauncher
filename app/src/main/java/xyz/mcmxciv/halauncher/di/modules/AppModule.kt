package xyz.mcmxciv.halauncher.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.mcmxciv.halauncher.data.cache.LocalCache
import xyz.mcmxciv.halauncher.data.cache.PreferencesLocalCache
import xyz.mcmxciv.halauncher.di.components.ViewComponent
import xyz.mcmxciv.halauncher.utils.LauncherResourceProvider
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Singleton

@Module(subcomponents = [ViewComponent::class])
class AppModule(private val context: Context) {
    @Singleton
    @Provides
    fun context(): Context = context

    @Singleton
    @Provides
    fun sharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    fun launcherResourceProvider(context: Context): ResourceProvider =
        LauncherResourceProvider(context)

    @Singleton
    @Provides
    fun packageManager(context: Context): PackageManager = context.packageManager

    @Singleton
    @Provides
    fun launcherApps(context: Context): LauncherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    @Singleton
    @Provides
    fun localCache(localCache: PreferencesLocalCache): LocalCache = localCache
}
