package xyz.mcmxciv.halauncher.data.integration

import retrofit2.http.Body
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.WebhookInfo

interface SecureIntegrationApi {
    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(@Body deviceInfo: DeviceInfo): WebhookInfo
}
