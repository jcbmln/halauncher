package xyz.mcmxciv.halauncher.sensors

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.integration.IntegrationUseCase
import java.util.concurrent.TimeUnit

@HiltWorker
class SensorCoroutineWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val integrationUseCase: IntegrationUseCase
) : CoroutineWorker(context, workerParameters) {
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
        fun start(context: Context, updateInterval: Long) {
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG)

            if (updateInterval > 0) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val worker = PeriodicWorkRequestBuilder<SensorCoroutineWorker>(
                    updateInterval,
                    TimeUnit.MINUTES
                ).setConstraints(constraints)
                    .addTag(TAG)
                    .build()

                WorkManager.getInstance(context).enqueue(worker)
            }
        }

        private const val TAG = "sensors"
    }
}
