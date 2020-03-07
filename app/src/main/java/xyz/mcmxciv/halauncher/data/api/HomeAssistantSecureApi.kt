package xyz.mcmxciv.halauncher.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.models.DiscoveryInfo
import xyz.mcmxciv.halauncher.models.DomainServices
import xyz.mcmxciv.halauncher.data.models.DeviceIntegration
import xyz.mcmxciv.halauncher.data.models.DeviceRegistration

interface HomeAssistantSecureApi {
    @GET("/api/discovery_info")
    suspend fun getDiscoveryInfo(): DiscoveryInfo

    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(@Body deviceRegistration: DeviceRegistration): DeviceIntegration

    @GET("/api/services")
    suspend fun getServices(): List<DomainServices>
}