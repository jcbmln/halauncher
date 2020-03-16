package xyz.mcmxciv.halauncher.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.mcmxciv.halauncher.di.components.ViewComponent
import xyz.mcmxciv.halauncher.utils.LauncherResourceProvider
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Singleton

@Singleton
@Module(subcomponents = [ViewComponent::class])
class AppModule(private val context: Context) {
    @Provides
    fun context(): Context = context

    @Provides
    fun sharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun launcherResourceProvider(context: Context): ResourceProvider =
        LauncherResourceProvider(context)

    @Provides
    fun packageManager(context: Context): PackageManager = context.packageManager

    @Provides
    fun launcherApps(context: Context): LauncherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
}