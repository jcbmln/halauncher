package xyz.mcmxciv.halauncher

import android.app.Application
import xyz.mcmxciv.halauncher.di.AppComponent
import xyz.mcmxciv.halauncher.di.AppModule
import xyz.mcmxciv.halauncher.di.DaggerAppComponent

class HalauncherApplication : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        component = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
        component.inject(this)

    }

    companion object {
        lateinit var instance: HalauncherApplication
            private set
    }
}
