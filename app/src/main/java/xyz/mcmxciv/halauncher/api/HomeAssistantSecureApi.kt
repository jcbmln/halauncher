package xyz.mcmxciv.halauncher.api

import retrofit2.http.Body
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.models.DeviceIntegration
import xyz.mcmxciv.halauncher.models.DeviceRegistration

interface HomeAssistantSecureApi {
    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(@Body request: DeviceRegistration): DeviceIntegration
}