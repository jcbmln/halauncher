package xyz.mcmxciv.halauncher.integration

import android.content.SharedPreferences
import androidx.core.content.edit
import okhttp3.HttpUrl.Companion.toHttpUrl
import timber.log.Timber
import xyz.mcmxciv.halauncher.integration.models.DeviceInfo
import xyz.mcmxciv.halauncher.integration.models.WebhookInfo
import xyz.mcmxciv.halauncher.integration.models.WebhookRequest
import xyz.mcmxciv.halauncher.sensors.models.Sensor
import xyz.mcmxciv.halauncher.sensors.models.SensorInfo
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import xyz.mcmxciv.halauncher.utils.Serializer
import xyz.mcmxciv.halauncher.utils.deserialize
import xyz.mcmxciv.halauncher.utils.serialize
import javax.inject.Inject

class IntegrationRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val secureIntegrationApi: SecureIntegrationApi,
    private val integrationApi: IntegrationApi
) {
    val webhookInfo: WebhookInfo?
        get() {
            val pref = sharedPreferences.getString(WEBHOOK_INFO_KEY, null)
            return pref?.let { Serializer.deserialize(it) }
        }

    var integrationOptOut: Boolean
        get() = sharedPreferences.getBoolean(INTEGRATION_OPT_OUT_KEY, false)
        set(value) = sharedPreferences.edit { putBoolean(INTEGRATION_OPT_OUT_KEY, value) }

    suspend fun registerDevice(deviceInfo: DeviceInfo): WebhookInfo =
        secureIntegrationApi.registerDevice(deviceInfo)

    suspend fun registerSensor(sensorInfo: SensorInfo) {
        val request = WebhookRequest("register_sensor", sensorInfo)
        val response = tryUrls { url -> integrationApi.webhookRequest(url, request) }

        if (response.isSuccessful || response.code() == 409) {
            val sensorIds = (
                sharedPreferences.getStringSet(SENSOR_IDS_KEY, null) ?: setOf()
            ).toMutableSet()
            sensorIds += sensorInfo.uniqueId
            sharedPreferences.edit { putStringSet(SENSOR_IDS_KEY, sensorIds) }
        }
    }

    suspend fun updateSensors(sensors: List<Sensor>): Map<String, Map<String, Any>> {
        val request = WebhookRequest(
            "update_sensor_states",
            sensors
        )
        val result = tryUrls { url -> integrationApi.updateSensors(url, request) }
        return if (result.isSuccessful) result.body()!!
            else throw IntegrationException()
    }

    fun saveWebhookInfo(webhookInfo: WebhookInfo) {
        sharedPreferences.edit {
            putString(WEBHOOK_INFO_KEY, Serializer.serialize(webhookInfo))
        }
    }

    fun clearRegisteredSensors() {
        sharedPreferences.edit { putStringSet(SENSOR_IDS_KEY, setOf()) }
    }

    private suspend fun <T> tryUrls(block: suspend (url: String) -> T?): T {
        val webhookInfo = webhookInfo ?: throw IntegrationException()
        val urls = mutableListOf<String>()
        val instanceUrl = sharedPreferences.getString(
                SettingsRepository.INSTANCE_URL_KEY,
                SettingsRepository.PLACEHOLDER_URL
            )!!

        webhookInfo.cloudhookUrl?.let { urls.add(it) }
        webhookInfo.remoteUiUrl?.let { urls.add(buildUrl(it, webhookInfo.webhookId)) }
        urls.add(buildUrl(instanceUrl, webhookInfo.webhookId))

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

    companion object {
        const val WEBHOOK_INFO_KEY = "webhook_info"
        const val INTEGRATION_OPT_OUT_KEY = "integration_opt_out"
        const val SENSOR_IDS_KEY = "sensor_ids"
    }
}
