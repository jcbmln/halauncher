package xyz.mcmxciv.halauncher

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import xyz.mcmxciv.halauncher.sensors.SensorCoroutineWorker

@HiltAndroidApp
class HalauncherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

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
