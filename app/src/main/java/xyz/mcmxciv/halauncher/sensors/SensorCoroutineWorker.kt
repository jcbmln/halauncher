package xyz.mcmxciv.halauncher.sensors

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.HalauncherApplication
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SensorCoroutineWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    private val component = HalauncherApplication.instance.component.serviceComponentBuilder()
        .build()

    @Inject
    lateinit var integrationUseCase: IntegrationUseCase

    init {
        component.inject(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (integrationUseCase.isDeviceIntegrated) updateSensors()
        Result.success()
    }

    private suspend fun updateSensors() {
        val sensorManagers = mutableListOf(
            BatterSensorManager(),
            NetworkSensorManager()
        )
        val sensors = sensorManagers.flatMap { m -> m.getSensors(context) }

        if (!integrationUseCase.updateSensors(sensors)) {
            sensorManagers.flatMap { m -> m.getSensorInfo(context) }.forEach {
                integrationUseCase.registerSensor(it)
            }
        }
    }

    companion object {
        fun start(context: Context, updateInterval: Long = 15) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val worker = PeriodicWorkRequestBuilder<SensorCoroutineWorker>(
                updateInterval,
                TimeUnit.MINUTES
            ).setConstraints(constraints)
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
            WorkManager.getInstance(context).enqueue(worker)
        }

        private const val TAG = "sensors"
    }
}
