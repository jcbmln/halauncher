package xyz.mcmxciv.halauncher.integration

import retrofit2.http.Body
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.integration.models.DeviceInfo
import xyz.mcmxciv.halauncher.integration.models.WebhookInfo

interface SecureIntegrationApi {
    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(@Body deviceInfo: DeviceInfo): WebhookInfo
}
