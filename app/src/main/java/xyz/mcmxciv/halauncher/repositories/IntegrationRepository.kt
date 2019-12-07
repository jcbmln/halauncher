package xyz.mcmxciv.halauncher.repositories

import dagger.Reusable
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.api.IntegrationApi
import xyz.mcmxciv.halauncher.dao.DeviceIntegrationDao
import xyz.mcmxciv.halauncher.models.DeviceIntegration
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import xyz.mcmxciv.halauncher.utils.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Reusable
class IntegrationRepository @Inject constructor(
    private val api: IntegrationApi,
    private val deviceIntegrationDao: DeviceIntegrationDao
) {
    suspend fun registerDevice(bearerToken: String, device: DeviceRegistration) {
        api.registerDevice(bearerToken, device).let { integration ->
            deviceIntegrationDao.insertDevice(DeviceIntegration(
                integration.webhookId,
                integration.cloudhookUrl,
                integration.remoteUiUrl,
                integration.secret
            ))
        }
    }
}
