package xyz.mcmxciv.halauncher.di.components

import dagger.Component
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.background.SensorUpdateWorker
import xyz.mcmxciv.halauncher.di.modules.AppModule
import xyz.mcmxciv.halauncher.di.modules.DataModule
import xyz.mcmxciv.halauncher.ui.main.MainActivity
import xyz.mcmxciv.halauncher.ui.main.MainActivityViewModel
import xyz.mcmxciv.halauncher.ui.main.shortcuts.ShortcutListAdapter
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun viewComponentBuilder(): ViewComponent.Builder

    fun mainActivityViewModel(): MainActivityViewModel

    fun inject(application: LauncherApplication)
    fun inject(activity: MainActivity)
    fun inject(sensorUpdateWorker: SensorUpdateWorker)
    fun inject(shortcutListAdapter: ShortcutListAdapter)
}