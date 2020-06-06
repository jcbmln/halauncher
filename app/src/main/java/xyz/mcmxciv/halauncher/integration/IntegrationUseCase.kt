package xyz.mcmxciv.halauncher.integration

import xyz.mcmxciv.halauncher.integration.models.DeviceInfo
import javax.inject.Inject

class IntegrationUseCase @Inject constructor(
    private val integrationRepository: IntegrationRepository
) {
    val isDeviceIntegrated: Boolean
        get() = integrationRepository.webhookInfo != null

    suspend fun registerDevice(deviceInfo: DeviceInfo) {
        integrationRepository.registerDevice(deviceInfo)
    }
}
