package xyz.mcmxciv.halauncher.data.repositories

import retrofit2.Response
import xyz.mcmxciv.halauncher.data.cache.PreferencesLocalCache
import xyz.mcmxciv.halauncher.data.api.HomeAssistantApi
import xyz.mcmxciv.halauncher.data.api.HomeAssistantSecureApi
import xyz.mcmxciv.halauncher.data.models.Config
import xyz.mcmxciv.halauncher.data.models.WebhookRequest
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.Sensor
import xyz.mcmxciv.halauncher.domain.models.SensorInfo
import xyz.mcmxciv.halauncher.domain.models.WebhookInfo
import xyz.mcmxciv.halauncher.models.DiscoveryInfo
import xyz.mcmxciv.halauncher.models.DomainServices
import javax.inject.Inject

class IntegrationRepository @Inject constructor(
    private val homeAssistantApi: HomeAssistantApi,
    private val homeAssistantSecureApi: HomeAssistantSecureApi,
    private val localCache: PreferencesLocalCache
) {
    suspend fun getDiscvoveryInfo(): DiscoveryInfo =
        homeAssistantSecureApi.getDiscoveryInfo()

    suspend fun registerDevice(deviceInfo: DeviceInfo): WebhookInfo =
        homeAssistantSecureApi.registerDevice(deviceInfo)

    suspend fun updateRegistration(
        url: String,
        deviceInfo: DeviceInfo
    ): Response<*> {
        val request = WebhookRequest(
            "update_registration",
            deviceInfo
        )
        return homeAssistantApi.updateRegistration(url, request)
    }

    suspend fun registerSensor(
        url: String,
        sensorInfo: SensorInfo
    ): Response<*> {
        val request = WebhookRequest(
            "register_sensor",
            sensorInfo
        )
        return homeAssistantApi.registerSensor(url, request)
    }

    suspend fun updateSensor(
        url: String,
        sensors: List<Sensor>
    ): Response<Map<String, Map<String, Any>>> {
        val request = WebhookRequest(
            "update_sensor_states",
            sensors
        )
        return homeAssistantApi.updateSensors(url, request)
    }

    suspend fun getServices(): List<DomainServices> =
        homeAssistantSecureApi.getServices()

    suspend fun getConfig(url: String): Response<Config> {
        val request =
            WebhookRequest("get_config", null)
        return homeAssistantApi.getConfig(url, request)
    }
}
