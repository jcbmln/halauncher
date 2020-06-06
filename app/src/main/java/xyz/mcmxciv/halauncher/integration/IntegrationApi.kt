package xyz.mcmxciv.halauncher.integration

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import xyz.mcmxciv.halauncher.integration.models.WebhookRequest

interface IntegrationApi {
    @POST
    suspend fun <T> webhookRequest(
        @Url url: String,
        @Body request: WebhookRequest<T>
    ): Response<ResponseBody>

    @POST
    suspend fun <T> updateSensors(
        @Url url: String,
        @Body request: WebhookRequest<T>
    ): Response<Map<String, Map<String, Any>>>
}
