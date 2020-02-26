package xyz.mcmxciv.halauncher.data.repositories

import retrofit2.Response
import xyz.mcmxciv.halauncher.data.api.HomeAssistantApi
import xyz.mcmxciv.halauncher.data.api.HomeAssistantSecureApi
import xyz.mcmxciv.halauncher.models.*
import javax.inject.Inject

class IntegrationRepository @Inject constructor(
    private val homeAssistantApi: HomeAssistantApi,
    private val homeAssistantSecureApi: HomeAssistantSecureApi
) {
    suspend fun getDiscvoveryInfo(): DiscoveryInfo =
        homeAssistantSecureApi.getDiscoveryInfo()

    suspend fun registerDevice(deviceRegistration: DeviceRegistration): DeviceIntegration =
        homeAssistantSecureApi.registerDevice(deviceRegistration)

    suspend fun getServices(): List<DomainServices> =
        homeAssistantSecureApi.getServices()

    suspend fun updateRegistration(
        url: String,
        deviceRegistration: DeviceRegistration
    ): Response<*> {
        val request = WebhookRequest("update_registration", deviceRegistration)
        return homeAssistantApi.updateRegistration(url, request)
    }

    suspend fun getConfig(url: String): Response<Config> {
        val request = WebhookRequest("get_config", null)
        return homeAssistantApi.getConfig(url, request)
    }
}