package xyz.mcmxciv.halauncher

import android.app.Application
import xyz.mcmxciv.halauncher.di.components.AppComponent
import xyz.mcmxciv.halauncher.di.components.DaggerAppComponent
import xyz.mcmxciv.halauncher.di.modules.AppModule
import xyz.mcmxciv.halauncher.di.modules.DataModule

class LauncherApplication : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .dataModule(DataModule())
            .build()
        instance = this
    }

    companion object {
        lateinit var instance: LauncherApplication
            private set
    }
}
