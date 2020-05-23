package xyz.mcmxciv.halauncher.data.authentication

import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.data.cache.LocalCache
import xyz.mcmxciv.halauncher.data.models.Token
import xyz.mcmxciv.halauncher.domain.models.Session
import xyz.mcmxciv.halauncher.domain.models.TokenResult
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val authenticationApi: AuthenticationApi,
    private val localCache: LocalCache
) {
    val authenticationUrl: String
        get() = localCache.instanceUrl.toHttpUrl()
            .newBuilder()
            .addPathSegments("auth/authorize")
            .addEncodedQueryParameter("response_type",
                RESPONSE_TYPE
            )
            .addEncodedQueryParameter("client_id",
                CLIENT_ID
            )
            .addEncodedQueryParameter("redirect_uri",
                REDIRECT_URI
            )
            .build()
            .toString()

    suspend fun getToken(authenticationCode: String): TokenResult {
        val response = authenticationApi.getToken(GRANT_TYPE_CODE, authenticationCode, CLIENT_ID)
        return if (response.isSuccessful) {
            val token: Token = response.body() ?: throw AuthenticationException()
            TokenResult.Success(token)
        } else {
            when (response.code()) {
                400 -> TokenResult.InvalidRequest
                403 -> TokenResult.InactiveUser
                else -> TokenResult.UnknownError
            }
        }
    }

    fun createSession(token: Token) {
        localCache.session = Session(token)
    }

    suspend fun refreshToken(refreshToken: String): TokenResult {
        val response = authenticationApi.refreshToken(GRANT_TYPE_REFRESH, refreshToken, CLIENT_ID)
        return if (response.isSuccessful) {
            TokenResult.Success(response.body()!!)
        } else {
            when (response.code()) {
                400 -> TokenResult.InvalidRequest
                else -> TokenResult.UnknownError
            }
        }
    }

    suspend fun revokeToken(refreshToken: String) {
        authenticationApi.revokeToken(refreshToken, REVOKE_ACTION)
    }

    companion object {
        const val CLIENT_ID = "https://halauncher.app"
        const val RESPONSE_TYPE = "code"
        const val GRANT_TYPE_CODE = "authorization_code"
        const val GRANT_TYPE_REFRESH = "refresh_token"
        const val REVOKE_ACTION = "revoke"
        const val REDIRECT_URI = "hass://auth"
        const val PLACEHOLDER_URL = "http://localhost:8123"
    }
}
