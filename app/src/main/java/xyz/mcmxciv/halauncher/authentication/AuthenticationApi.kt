package xyz.mcmxciv.halauncher.authentication

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.authentication.models.Token

interface AuthenticationApi {
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
}
