package xyz.mcmxciv.halauncher.data.integration

import okhttp3.HttpUrl.Companion.toHttpUrl
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.IntegrationException
import xyz.mcmxciv.halauncher.data.LocalCache
import xyz.mcmxciv.halauncher.data.models.WebhookRequest
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.Sensor
import xyz.mcmxciv.halauncher.domain.models.SensorInfo
import javax.inject.Inject

class IntegrationRepository @Inject constructor(
    private val integrationApi: IntegrationApi,
    private val secureIntegrationApi: SecureIntegrationApi,
    private val localCache: LocalCache
) {
    suspend fun registerDevice(deviceInfo: DeviceInfo) {
        localCache.webhookInfo = secureIntegrationApi.registerDevice(deviceInfo)
    }

    suspend fun updateRegistration(deviceInfo: DeviceInfo): Boolean {
        val request = WebhookRequest("update_registration", deviceInfo)
        val result = tryUrls { url -> integrationApi.webhookRequest(url, request) }
        return result.isSuccessful
    }

    suspend fun registerSensor(sensorInfo: SensorInfo) {
        val request = WebhookRequest("register_sensor", sensorInfo)
        val response = tryUrls { url -> integrationApi.webhookRequest(url, request) }

        if (response.isSuccessful || response.code() == 409) {
            localCache.sensorIds += sensorInfo.uniqueId
        }
    }

    suspend fun updateSensor(sensors: List<Sensor>): Map<String, Map<String, Any>> {
        val request = WebhookRequest(
            "update_sensor_states",
            sensors
        )
        val result = tryUrls { url -> integrationApi.updateSensors(url, request) }
        return if (result.isSuccessful) result.body()!!
            else throw IntegrationException()
    }

    fun clearRegisteredSensors() {
        localCache.sensorIds = setOf()
    }

    private suspend fun <T> tryUrls(block: suspend (url: String) -> T?): T {
        val webhookInfo = localCache.webhookInfo ?: throw IntegrationException()
        val urls = mutableListOf<String>()

        webhookInfo.cloudhookUrl?.let { urls.add(it) }
        webhookInfo.remoteUiUrl?.let { urls.add(buildUrl(it, webhookInfo.webhookId)) }
        urls.add(buildUrl(localCache.baseUrl, webhookInfo.webhookId))

        var result: T? = null

        for (url in urls) {
            try {
                result = block(url)
                if (result != null) break
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }

        return result ?: throw IntegrationException()
    }

    private fun buildUrl(url: String, id: String): String =
        url.toHttpUrl().newBuilder()
            .addPathSegments("api/webhook/$id")
            .build()
            .toString()
}
