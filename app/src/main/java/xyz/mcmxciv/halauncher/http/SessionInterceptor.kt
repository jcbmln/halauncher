package xyz.mcmxciv.halauncher.http

import android.content.SharedPreferences
import androidx.core.content.edit
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import xyz.mcmxciv.halauncher.authentication.AuthenticationException
import xyz.mcmxciv.halauncher.authentication.AuthenticationRepository
import xyz.mcmxciv.halauncher.authentication.models.Session
import xyz.mcmxciv.halauncher.authentication.models.Token
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import xyz.mcmxciv.halauncher.utils.Serializer
import xyz.mcmxciv.halauncher.utils.deserialize
import xyz.mcmxciv.halauncher.utils.serialize
import java.lang.IllegalStateException
import java.time.Instant

class SessionInterceptor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val instanceUrl = sharedPreferences.getString(
            SettingsRepository.INSTANCE_URL_KEY,
            null
        ) ?: throw IllegalStateException()
        val authenticationUrl = instanceUrl.toHttpUrl()
            .newBuilder()
            .addPathSegment("auth/token")
            .build()
            .toString()
        val sessionString = sharedPreferences.getString(
            AuthenticationRepository.SESSION_KEY, null
        ) ?: throw IllegalStateException()
        val session = Serializer.deserialize<Session>(sessionString) ?:
            throw IllegalStateException()

        val accessToken = if (!session.isExpired) {
            session.accessToken
        } else {
            val requestBody = getRefreshTokenRequestBody(session.refreshToken)
            val refreshRequest = chain.request().newBuilder()
                .url(authenticationUrl)
                .post(requestBody)
                .build()
            val refreshResponse = chain.proceedRevokeSessionOnError(
                refreshRequest,
                session.accessToken
            )

            if (refreshResponse.isSuccessful) {
                val token = Serializer.deserialize<Token>(refreshResponse.body.toString())!!
                val refreshedSession =
                    Session(
                        token.accessToken,
                        Instant.now().epochSecond + token.expiresIn,
                        session.refreshToken,
                        token.tokenType
                    )

                sharedPreferences.edit {
                    putString(
                        AuthenticationRepository.SESSION_KEY,
                        Serializer.serialize(refreshedSession)
                    )
                }

                refreshedSession.accessToken
            } else throw AuthenticationException()
        }

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceedRevokeSessionOnError(request, accessToken)
    }

    private fun Interceptor.Chain.proceedRevokeSessionOnError(
        request: Request,
        accessToken: String
    ): Response {
        val response = proceed(request)

        return if (response.code == 401) {
            val requestBody = getRevokeRequestBody(accessToken)
            val revokeRequest = request.newBuilder()
                .url(request.url)
                .post(requestBody)
                .build()
            proceed(revokeRequest)
        } else response
    }

    private fun getRefreshTokenRequestBody(refreshToken: String): RequestBody =
        FormBody.Builder()
            .add("grant_type", AuthenticationRepository.GRANT_TYPE_REFRESH)
            .add("refresh_token", refreshToken)
            .add("client_id", AuthenticationRepository.CLIENT_ID)
            .build()

    private fun getRevokeRequestBody(accessToken: String): RequestBody =
        FormBody.Builder()
            .add("token", accessToken)
            .add("action", AuthenticationRepository.REVOKE_ACTION)
            .build()
}
