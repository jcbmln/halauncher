package xyz.mcmxciv.halauncher.repositories

import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.interfaces.IntegrationApi
import xyz.mcmxciv.halauncher.models.Device
import xyz.mcmxciv.halauncher.utils.ApiFactory
import xyz.mcmxciv.halauncher.utils.AppPreferences

class IntegrationRepository {
    private val api = ApiFactory.createApi(prefs.url, IntegrationApi::class.java)

    suspend fun registerDevice(device: Device) {
        api.registerDevice(AuthenticationRepository().bearerToken(), device).let {
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