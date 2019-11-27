package xyz.mcmxciv.halauncher.repositories

import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.services.IntegrationService
import xyz.mcmxciv.halauncher.models.Device
import xyz.mcmxciv.halauncher.services.ServiceFactory
import xyz.mcmxciv.halauncher.utils.AppPreferences

class IntegrationRepository {
    private val service = ServiceFactory.createService(prefs.url, IntegrationService::class.java)

    suspend fun registerDevice(device: Device) {
        service.registerDevice(AuthenticationRepository().bearerToken(), device).let {
            prefs.cloudhookUrl = it.cloudhookUrl
            prefs.remoteUiUrl = it.remoteUiUrl
            prefs.secret = it.secret
            prefs.webhookId = it.webhookId
        }
    }

    companion object {
        private val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())
    }
}