package xyz.mcmxciv.halauncher.di

import dagger.Subcomponent
import xyz.mcmxciv.halauncher.sensors.SensorCoroutineWorker

@ServiceScope
@Subcomponent
interface ServiceComponent {
    fun inject(sensorCoroutineWorker: SensorCoroutineWorker)

    @Subcomponent.Builder
    interface Builder {
        fun build(): ServiceComponent
    }
}
