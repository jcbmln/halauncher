package xyz.mcmxciv.halauncher.di.components

import dagger.Component
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.modules.AppModule
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun fragmentBuilder(): FragmentComponent.Builder

    fun inject(application: LauncherApplication)

    fun appSettings(): AppSettings
}