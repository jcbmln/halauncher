package xyz.mcmxciv.halauncher.data.authentication

import xyz.mcmxciv.halauncher.data.models.TokenResult
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val authenticationApi: AuthenticationApi
) {
    suspend fun getToken(authenticationCode: String): TokenResult {
        val response = authenticationApi.getToken(GRANT_TYPE_CODE, authenticationCode, CLIENT_ID)
        return if (response.isSuccessful) {
            TokenResult.Success(response.body()!!)
        } else {
            when (response.code()) {
                400 -> TokenResult.InvalidRequest
                403 -> TokenResult.InactiveUser
                else -> TokenResult.Error(response.message())
            }
        }
    }

    suspend fun refreshToken(refreshToken: String): TokenResult {
        val response = authenticationApi.refreshToken(GRANT_TYPE_REFRESH, refreshToken, CLIENT_ID)
        return if (response.isSuccessful) {
            TokenResult.Success(response.body()!!)
        } else {
            when (response.code()) {
                400 -> TokenResult.InvalidRequest
                else -> TokenResult.Error(response.message())
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
    }
}