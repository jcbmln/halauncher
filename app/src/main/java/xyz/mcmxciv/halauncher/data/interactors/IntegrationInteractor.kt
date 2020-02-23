package xyz.mcmxciv.halauncher.data.interactors

import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.data.IntegrationException
import xyz.mcmxciv.halauncher.data.repositories.IntegrationRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
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

    private suspend fun tryUrls(block: suspend (url: String) -> Boolean) {
        val integration = localStorageRepository.deviceIntegration ?: throw IntegrationException()
        val urls = ArrayList<String>()
        val cloudhookUrl = integration.cloudhookUrl
        val remoteUiUrl = integration.remoteUiUrl?.let { buildUrl(it, integration.webhookId) }
        val localUrl = buildUrl(localStorageRepository.baseUrl, integration.webhookId)

        cloudhookUrl?.let { urls.add(it) }
        remoteUiUrl?.let { urls.add(it) }
        urls.add(localUrl)

        for (url in urls) {
            try {
                if (block(url)) return
            } catch (ex: Exception) {}
        }

        throw IntegrationException()
    }

    private fun buildUrl(url: String, id: String): String =
        url.toHttpUrl().newBuilder()
            .addPathSegments("api/webhook/${id}")
            .build()
            .toString()
}