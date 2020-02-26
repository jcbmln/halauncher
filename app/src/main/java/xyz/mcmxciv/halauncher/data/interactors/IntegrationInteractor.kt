package xyz.mcmxciv.halauncher.data.interactors

import okhttp3.HttpUrl.Companion.toHttpUrl
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.IntegrationException
import xyz.mcmxciv.halauncher.data.repositories.IntegrationRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.models.Config
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import java.lang.Exception
import javax.inject.Inject

class IntegrationInteractor @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
    private val integrationRepository: IntegrationRepository
) {
    suspend fun registerDevice(deviceRegistration: DeviceRegistration) {
        localStorageRepository.deviceRegistration = deviceRegistration
        localStorageRepository.deviceIntegration =
            integrationRepository.registerDevice(deviceRegistration)
    }

    suspend fun updateRegistration(
        appVersion: String?,
        deviceName: String?,
        osVersion: String?,
        appData: Map<String, String>?
    ) {
        val cachedDeviceRegistration = localStorageRepository.deviceRegistration
            ?: throw IntegrationException()

        val newDeviceRegistration = DeviceRegistration(
            appVersion = appVersion ?: cachedDeviceRegistration.appVersion,
            deviceName = deviceName ?: cachedDeviceRegistration.deviceName,
            osVersion = osVersion ?: cachedDeviceRegistration.osName,
            appData =  appData ?: cachedDeviceRegistration.appData
        )

        tryUrls { url ->
            val response = integrationRepository.updateRegistration(url, newDeviceRegistration)
            return@tryUrls response.isSuccessful
        }
    }

    suspend fun getConfig(): Config {
        return tryUrls { url ->
            val response = integrationRepository.getConfig(url)
            return@tryUrls if (response.isSuccessful) {
                response.body()
            } else null
        }
    }

    private suspend fun <T> tryUrls(block: suspend (url: String) -> T?): T {
        val integration = localStorageRepository.deviceIntegration ?: throw IntegrationException()
        val urls = ArrayList<String>()
        val cloudhookUrl = integration.cloudhookUrl
        val remoteUiUrl = integration.remoteUiUrl?.let { buildUrl(it, integration.webhookId) }
        val localUrl = buildUrl(localStorageRepository.baseUrl, integration.webhookId)

        cloudhookUrl?.let { urls.add(it) }
        remoteUiUrl?.let { urls.add(it) }
        urls.add(localUrl)

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