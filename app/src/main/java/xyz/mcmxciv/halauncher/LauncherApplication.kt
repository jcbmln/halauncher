package xyz.mcmxciv.halauncher

import android.app.Application
import xyz.mcmxciv.halauncher.di.components.AppComponent
import xyz.mcmxciv.halauncher.di.components.DaggerAppComponent
import xyz.mcmxciv.halauncher.di.modules.AppModule
import xyz.mcmxciv.halauncher.utils.AppSettings
import javax.inject.Inject

class LauncherApplication : Application() {
    lateinit var component: AppComponent

    @Inject
    lateinit var appSettings: AppSettings

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
        component.inject(this)

        instance = this
    }

    companion object {
        lateinit var instance: LauncherApplication
    }
}