package xyz.mcmxciv.halauncher.di.components

import dagger.Component
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.modules.AppModule
import xyz.mcmxciv.halauncher.di.modules.DataModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun viewComponentBuilder(): ViewComponent.Builder

    fun inject(application: LauncherApplication)
}