package xyz.mcmxciv.halauncher.api

import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.models.Token
import xyz.mcmxciv.halauncher.utils.AppPreferences

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
    ): Token

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun revokeToken(
        @Field("refresh_token") refreshToken: String,
        @Field("action") action: String
    )

    companion object {
        const val CLIENT_ID = "https://halauncher.app"
        const val RESPONSE_TYPE = "code"
        const val GRANT_TYPE_CODE = "authorization_code"
        const val GRANT_TYPE_REFRESH = "refresh_token"
        const val REVOKE_ACTION = "revoke"
        const val REDIRECT_URI = "hass://auth"

        private val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())

        val authenticationUrl: String
            get() {
                return prefs.url.toHttpUrl()
                    .newBuilder()
                    .addPathSegments("auth/authorize")
                    .addEncodedQueryParameter("response_type", RESPONSE_TYPE)
                    .addEncodedQueryParameter("client_id", CLIENT_ID)
                    .addEncodedQueryParameter("redirect_uri", REDIRECT_URI)
                    .build()
                    .toString()
            }
    }
}