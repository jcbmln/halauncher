package xyz.mcmxciv.halauncher.di.components

import dagger.Component
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.modules.AppModule
import xyz.mcmxciv.halauncher.di.modules.DataModule
import xyz.mcmxciv.halauncher.sensors.SensorUpdateWorker
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.ui.main.MainActivity
import xyz.mcmxciv.halauncher.ui.home.shortcuts.ShortcutListAdapter
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun viewComponentBuilder(): ViewComponent.Builder

    fun inject(application: LauncherApplication)
    fun inject(sensorUpdateWorker: SensorUpdateWorker)
    fun inject(shortcutListAdapter: ShortcutListAdapter)
    fun inject(hassTheme: HassTheme)
}
