package xyz.mcmxciv.halauncher.data.api

import retrofit2.Response
import retrofit2.http.*
import xyz.mcmxciv.halauncher.models.*

interface HomeAssistantApi {
    @FormUrlEncoded
    @POST("auth/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("client_id") clientId: String
    ): Token

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun refreshToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String
    ): Response<Token>

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun revokeToken(
        @Field("token") refreshToken: String,
        @Field("action") action: String
    )

    @POST
    suspend fun updateRegistration(
        @Url url: String,
        @Body request: IntegrationRequest<DeviceRegistration>
    ): Response<*>
}