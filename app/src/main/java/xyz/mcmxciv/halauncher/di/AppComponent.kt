package xyz.mcmxciv.halauncher.di

import dagger.Component
import xyz.mcmxciv.halauncher.HalauncherApplication

@AppScope
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun viewComponentBuilder(): ViewComponent.Builder
    fun serviceComponentBuilder(): ServiceComponent.Builder

    fun inject(app: HalauncherApplication)
}
