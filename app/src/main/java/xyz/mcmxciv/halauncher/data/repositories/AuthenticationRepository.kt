package xyz.mcmxciv.halauncher.data.repositories

import org.json.JSONObject
import org.threeten.bp.Instant
import xyz.mcmxciv.halauncher.data.AuthenticationException
import xyz.mcmxciv.halauncher.data.api.HomeAssistantApi
import xyz.mcmxciv.halauncher.models.Session
import xyz.mcmxciv.halauncher.models.SessionState
import xyz.mcmxciv.halauncher.models.Token
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val homeAssistantApi: HomeAssistantApi
) {
//    private val session: Session
//        get() = localStorageRepository.session ?: throw AuthenticationException()

    suspend fun getToken(authenticationCode: String): Token {
        return homeAssistantApi.getToken(GRANT_TYPE_CODE, authenticationCode, CLIENT_ID)
    }

    suspend fun revokeToken(refreshToken: String) {
        homeAssistantApi.revokeToken(refreshToken, REVOKE_ACTION)
    }

//    suspend fun validateSession(): SessionState {
//        return if (localStorageRepository.session == null) {
//            if (localStorageRepository.baseUrl == LocalStorageRepository.PLACEHOLDER_URL) SessionState.NEW_USER
//            else SessionState.UNAUTHENTICATED
//        }
//        else {
//            val response = homeAssistantApi.refreshToken(
//                GRANT_TYPE_REFRESH,
//                session.refreshToken,
//                CLIENT_ID
//            )
//
//            if (response.isSuccessful) {
//                val token = response.body() ?: throw AuthenticationException()
//                localStorageRepository.session = Session(
//                    token.accessToken,
//                    Instant.now().epochSecond + token.expiresIn,
//                    session.refreshToken,
//                    token.tokenType
//                )
//
//                SessionState.AUTHENTICATED
//            }
//            else {
//                revokeToken()
//                SessionState.UNAUTHENTICATED
//            }
//        }
//    }

    companion object {
        const val CLIENT_ID = "https://halauncher.app"
        const val RESPONSE_TYPE = "code"
        const val GRANT_TYPE_CODE = "authorization_code"
        const val GRANT_TYPE_REFRESH = "refresh_token"
        const val REVOKE_ACTION = "revoke"
        const val REDIRECT_URI = "hass://auth"
    }
}