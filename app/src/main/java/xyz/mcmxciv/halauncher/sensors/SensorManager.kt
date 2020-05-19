package xyz.mcmxciv.halauncher.sensors

import android.content.Context
import xyz.mcmxciv.halauncher.domain.models.Sensor
import xyz.mcmxciv.halauncher.domain.models.SensorInfo

interface SensorManager {
    fun getSensorInfo(context: Context): List<SensorInfo>
    fun getSensors(context: Context): List<Sensor>
}
