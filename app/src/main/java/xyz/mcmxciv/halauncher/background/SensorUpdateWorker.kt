package xyz.mcmxciv.halauncher.background

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.data.interactors.IntegrationInteractor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SensorUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var integrationInteractor: IntegrationInteractor

    init {
        LauncherApplication.instance.component.inject(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        integrationInteractor.updateSensors()
        return@withContext Result.success()
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