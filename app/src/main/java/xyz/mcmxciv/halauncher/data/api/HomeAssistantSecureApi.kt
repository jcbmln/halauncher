package xyz.mcmxciv.halauncher.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.WebhookInfo
import xyz.mcmxciv.halauncher.models.DiscoveryInfo
import xyz.mcmxciv.halauncher.models.DomainServices

interface HomeAssistantSecureApi {
    @GET("/api/discovery_info")
    suspend fun getDiscoveryInfo(): DiscoveryInfo

    @POST("/api/mobile_app/registrations")
    suspend fun registerDevice(@Body deviceInfo: DeviceInfo): WebhookInfo

    @GET("/api/services")
    suspend fun getServices(): List<DomainServices>
}
