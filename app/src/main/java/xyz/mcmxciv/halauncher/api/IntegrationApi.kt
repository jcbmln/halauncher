package xyz.mcmxciv.halauncher.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.models.DeviceRegistration
import xyz.mcmxciv.halauncher.models.Integration

interface IntegrationApi {
    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(
        @Header("Authorization") auth: String,
        @Body request: DeviceRegistration
    ): Integration
}
