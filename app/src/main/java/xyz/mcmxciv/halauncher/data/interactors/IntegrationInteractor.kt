package xyz.mcmxciv.halauncher.data.interactors

import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrl
import timber.log.Timber
import xyz.mcmxciv.halauncher.sensors.SensorUpdateWorker
import xyz.mcmxciv.halauncher.data.IntegrationException
import xyz.mcmxciv.halauncher.domain.models.Sensor
import xyz.mcmxciv.halauncher.domain.models.SensorInfo
import xyz.mcmxciv.halauncher.data.repositories.IntegrationRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.data.repositories.SensorRepository
import xyz.mcmxciv.halauncher.data.models.Config
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Inject

class IntegrationInteractor @Inject constructor(
    private val context: Context,
    private val localStorageRepository: LocalStorageRepository,
    private val integrationRepository: IntegrationRepository,
    private val sensorRepository: SensorRepository
) {
    val deviceInfo: DeviceInfo
        get() = localStorageRepository.deviceInfo ?: throw IllegalStateException()

    var sensorUpdateInterval: Long
        get() = localStorageRepository.sensorUpdateInterval.toLong()
        set(value) {
            localStorageRepository.sensorUpdateInterval = value.toInt()
            SensorUpdateWorker.start(context, value)
        }

    suspend fun registerDevice(deviceInfo: DeviceInfo) {
        localStorageRepository.deviceInfo = deviceInfo
        localStorageRepository.webhookInfo =
            integrationRepository.registerDevice(deviceInfo)
        SensorUpdateWorker.start(context)
    }

    suspend fun updateRegistration(
        deviceName: String?,
        appVersion: String? = null,
        osVersion: String? = null,
        appData: Map<String, String>? = null
    ) {
        val cachedDeviceRegistration = localStorageRepository.deviceInfo
            ?: throw IntegrationException()

        val newDeviceRegistration =
            DeviceInfo(
                appVersion = appVersion ?: cachedDeviceRegistration.appVersion,
                deviceName = deviceName ?: cachedDeviceRegistration.deviceName,
                osVersion = osVersion ?: cachedDeviceRegistration.osName,
                appData = appData ?: cachedDeviceRegistration.appData
            )

        tryUrls { url ->
            val response = integrationRepository.updateRegistration(url, newDeviceRegistration)
            return@tryUrls response.isSuccessful
        }
    }

    suspend fun updateSensors() {
        val sensors = mutableListOf<Sensor>()
        sensorRepository.getBatterySensor()?.let { sensors.add(it) }

        tryUrls { url ->
            val response = integrationRepository.updateSensor(url, sensors)
            response.body()?.let { results ->
                results["battery_level"]?.let { result ->
                    if (result["success"] == false) {
                        registerBatterySensor()
                    }
                }
                results["wifi_connection"]?.let { result ->
                    if (result["success"] == false) {
                        registerNetworkSensor()
                    }
                }
            }

            return@tryUrls response.isSuccessful
        }
    }

    private suspend fun registerBatterySensor() {
        sensorRepository.getBatterySensor()?.let { sensor ->
            val registration = SensorInfo(
                sensor,
                "battery",
                "Battery Level",
                "%"
            )

            return@let tryUrls { url ->
                val response = integrationRepository.registerSensor(url, registration)
                return@tryUrls response.isSuccessful
            }
        }
    }

    private suspend fun registerNetworkSensor() {
        val registration = SensorInfo(
            sensorRepository.getNetworkSensor(),
            "WiFi Connection"
        )

        tryUrls { url ->
            val response = integrationRepository.registerSensor(url, registration)
            return@tryUrls response.isSuccessful
        }
    }

    suspend fun getConfig(): Config? {
        return localStorageRepository.webhookInfo?.let {
            return@let tryUrls { url ->
                val response = integrationRepository.getConfig(url)
                return@tryUrls if (response.isSuccessful) {
                    response.body()
                } else null
            }
        }
    }

    private suspend fun <T> tryUrls(block: suspend (url: String) -> T?): T {
        val integration = localStorageRepository.webhookInfo ?: throw IntegrationException()
        val urls = ArrayList<String>()

        integration.cloudhookUrl?.let { urls.add(it) }
        integration.remoteUiUrl?.let { urls.add(buildUrl(it, integration.webhookId)) }
        urls.add(buildUrl(localStorageRepository.baseUrl, integration.webhookId))

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
            .addPathSegments("api/webhook/${id}")
            .build()
            .toString()
}