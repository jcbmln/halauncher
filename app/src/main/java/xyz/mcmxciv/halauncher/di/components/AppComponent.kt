package xyz.mcmxciv.halauncher.di.components

import dagger.Component
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.modules.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun fragmentBuilder(): FragmentComponent.Builder

    fun inject(application: LauncherApplication)
}