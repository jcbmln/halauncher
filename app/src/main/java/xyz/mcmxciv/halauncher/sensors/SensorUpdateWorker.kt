package xyz.mcmxciv.halauncher.sensors

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.data.interactors.IntegrationInteractor
import xyz.mcmxciv.halauncher.domain.integration.IntegrationUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SensorUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var integrationUseCase: IntegrationUseCase

    init {
        LauncherApplication.instance.component.inject(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        updateSensors()
        return@withContext Result.success()
    }

    private suspend fun updateSensors() {
        val sensorManagers = mutableListOf(BatterySensorManager(), NetworkSensorManager())
        val sensors = sensorManagers.flatMap { m -> m.getSensors(context) }

        if (!integrationUseCase.updateSensors(sensors)) {
            sensorManagers.flatMap { m -> m.getSensorInfo(context) }.forEach { si ->
                integrationUseCase.registerSensors(si)
            }
        }
    }

    companion object {
        fun start(context: Context, updateInterval: Long = 15) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val worker = PeriodicWorkRequestBuilder<SensorUpdateWorker>(
                updateInterval,
                TimeUnit.MINUTES
            ).setConstraints(constraints)
                .addTag("sensors")
                .build()

            WorkManager.getInstance(context).cancelAllWorkByTag("sensors")
            WorkManager.getInstance(context).enqueue(worker)
        }
    }


}