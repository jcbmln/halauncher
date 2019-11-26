package xyz.mcmxciv.halauncher.interfaces

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.models.Device
import xyz.mcmxciv.halauncher.models.Integration

interface IntegrationApi {
    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(
        @Header("Authorization") auth: String,
        @Body request: Device
    ): Integration
}