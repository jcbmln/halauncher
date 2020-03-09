package xyz.mcmxciv.halauncher.di.components

import dagger.Component
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.SensorUpdateWorker
import xyz.mcmxciv.halauncher.di.modules.AppModule
import xyz.mcmxciv.halauncher.di.modules.DataModule
import xyz.mcmxciv.halauncher.ui.MainActivity
import xyz.mcmxciv.halauncher.ui.MainActivityViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun viewComponentBuilder(): ViewComponent.Builder

    fun mainActivityViewModel(): MainActivityViewModel

    fun inject(application: LauncherApplication)
    fun inject(activity: MainActivity)
    fun inject(sensorUpdateWorker: SensorUpdateWorker)
}