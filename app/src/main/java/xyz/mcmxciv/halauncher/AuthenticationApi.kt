package xyz.mcmxciv.halauncher

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.models.AuthToken

interface AuthenticationApi {
    @FormUrlEncoded
    @POST("auth/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("client_id") clientId: String
    ): AuthToken

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun refreshToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String
    ): AuthToken

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun revokeToken(
        @Field("refresh_token") refreshToken: String,
        @Field("action") action: String
    )
}