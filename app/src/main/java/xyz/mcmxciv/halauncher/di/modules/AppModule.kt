package xyz.mcmxciv.halauncher.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.mcmxciv.halauncher.di.components.FragmentComponent
import javax.inject.Singleton

@Module(subcomponents = [FragmentComponent::class])
class AppModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    fun providePackagaManager(context: Context): PackageManager = context.packageManager
}