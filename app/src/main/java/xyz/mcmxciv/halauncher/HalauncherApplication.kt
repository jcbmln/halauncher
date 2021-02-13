package xyz.mcmxciv.halauncher

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase
import xyz.mcmxciv.halauncher.sensors.SensorCoroutineWorker
import javax.inject.Inject

@HiltAndroidApp
class HalauncherApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var integrationUseCase: IntegrationUseCase

    override fun onCreate() {
        super.onCreate()
        instance = this

        startWorkers()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    fun startWorkers(sensorUpdateInterval: Long? = null) {
        SensorCoroutineWorker.start(
            applicationContext,
            sensorUpdateInterval ?: integrationUseCase.sensorUpdateInterval
        )
    }

    companion object {
        lateinit var instance: HalauncherApplication
            private set
    }
}
