package xyz.mcmxciv.halauncher.services

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.models.Device
import xyz.mcmxciv.halauncher.models.Integration

interface IntegrationService {
    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(
        @Header("Authorization") auth: String,
        @Body request: Device
    ): Integration
}