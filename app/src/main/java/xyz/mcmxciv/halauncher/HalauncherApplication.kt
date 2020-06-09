package xyz.mcmxciv.halauncher

import android.app.Application
import xyz.mcmxciv.halauncher.di.AppComponent
import xyz.mcmxciv.halauncher.di.AppModule
import xyz.mcmxciv.halauncher.di.DaggerAppComponent
import xyz.mcmxciv.halauncher.sensors.SensorCoroutineWorker

class HalauncherApplication : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        component = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
        component.inject(this)
        startWorkers()
    }

    fun startWorkers() {
        SensorCoroutineWorker.start(applicationContext)
    }

    companion object {
        lateinit var instance: HalauncherApplication
            private set
    }
}
