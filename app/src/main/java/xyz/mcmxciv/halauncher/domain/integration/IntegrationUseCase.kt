package xyz.mcmxciv.halauncher.domain.integration

import xyz.mcmxciv.halauncher.data.integration.IntegrationRepository
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.Sensor
import xyz.mcmxciv.halauncher.domain.models.SensorInfo
import javax.inject.Inject

class IntegrationUseCase @Inject constructor(
    private val integrationRepository: IntegrationRepository
) {
    suspend fun registerDevice(deviceInfo: DeviceInfo) {
        integrationRepository.registerDevice(deviceInfo)
    }

    suspend fun updateRegistration(deviceInfo: DeviceInfo) {
        integrationRepository.updateRegistration(deviceInfo)
    }

    suspend fun registerSensors(sensorInfo: SensorInfo) {
        integrationRepository.registerSensor(sensorInfo)
    }

    suspend fun updateSensors(sensors: List<Sensor>): Boolean {
        val result = integrationRepository.updateSensor(sensors)

        if (result.any { r -> r.value["success"] == false }) {
            integrationRepository.clearRegisteredSensors()
            return false
        }

        return true
    }
}