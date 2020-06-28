package xyz.mcmxciv.halauncher

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import xyz.mcmxciv.halauncher.sensors.SensorCoroutineWorker
import javax.inject.Inject

@HiltAndroidApp
class HalauncherApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        instance = this

        startWorkers()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    fun startWorkers() {
//        SensorCoroutineWorker.start(applicationContext)
    }

    companion object {
        lateinit var instance: HalauncherApplication
            private set
    }
}
