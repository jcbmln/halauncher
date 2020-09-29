package xyz.mcmxciv.halauncher.integration

import xyz.mcmxciv.halauncher.integration.models.DeviceInfo
import xyz.mcmxciv.halauncher.sensors.models.Sensor
import xyz.mcmxciv.halauncher.sensors.models.SensorInfo
import javax.inject.Inject

class IntegrationUseCase @Inject constructor(
    private val integrationRepository: IntegrationRepository
) {
    val isDeviceIntegrated: Boolean
        get() = integrationRepository.webhookInfo != null

    val sensorUpdateInterval: Long
        get() = integrationRepository.sensorUpdateInterval

    val integrationEnabled: Boolean
        get() = integrationRepository.integrationEnabled

    fun validateIntegration(): Boolean =
        integrationRepository.webhookInfo != null || !integrationRepository.integrationEnabled

    suspend fun registerDevice(deviceInfo: DeviceInfo) {
        val webhookInfo = integrationRepository.registerDevice(deviceInfo)
        integrationRepository.saveWebhookInfo(webhookInfo)
        integrationRepository.integrationEnabled = true
    }

    suspend fun registerSensor(sensorInfo: SensorInfo) {
        integrationRepository.registerSensor(sensorInfo)
    }

    suspend fun updateSensors(sensors: List<Sensor>): Boolean {
        val result = integrationRepository.updateSensors(sensors)

        return if (result.all { r -> r.value["success"] == true }) true
        else {
            integrationRepository.clearRegisteredSensors()
            false
        }
    }
}
