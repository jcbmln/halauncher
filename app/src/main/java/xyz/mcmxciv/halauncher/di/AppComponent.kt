package xyz.mcmxciv.halauncher.di

import dagger.Component
import xyz.mcmxciv.halauncher.HalauncherApplication
import xyz.mcmxciv.halauncher.sensors.SensorCoroutineWorker

@AppScope
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun viewComponentBuilder(): ViewComponent.Builder
    fun serviceComponentBuilder(): ServiceComponent.Builder

    fun inject(app: HalauncherApplication)
}
