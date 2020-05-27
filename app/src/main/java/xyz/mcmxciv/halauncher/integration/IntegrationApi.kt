package xyz.mcmxciv.halauncher.integration

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import xyz.mcmxciv.halauncher.data.models.WebhookRequest

interface IntegrationApi {
    @POST
    suspend fun webhookRequest(
        @Url url: String,
        @Body request: WebhookRequest
    ): Response<ResponseBody>

    @POST
    suspend fun updateSensors(
        @Url url: String,
        @Body request: WebhookRequest
    ): Response<Map<String, Map<String, Any>>>
}
