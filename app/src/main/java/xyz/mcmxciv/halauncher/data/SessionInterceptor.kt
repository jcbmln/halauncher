package xyz.mcmxciv.halauncher.data

import com.squareup.moshi.Moshi
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.threeten.bp.Instant
import xyz.mcmxciv.halauncher.data.interactors.UrlInteractor
import xyz.mcmxciv.halauncher.data.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.models.Session
import xyz.mcmxciv.halauncher.models.Token

class SessionInterceptor constructor(
    private val urlInteractor: UrlInteractor,
    private val localStorageRepository: LocalStorageRepository
) : Interceptor {
    private val authUrl: String
        get() = urlInteractor.baseUrl.toHttpUrl()
            .newBuilder()
            .addPathSegments("auth/token")
            .build()
            .toString()

    override fun intercept(chain: Interceptor.Chain): Response {
        val session = localStorageRepository.session ?: throw AuthenticationException()
        val originalRequest = chain.request()

        return if (!session.isExpired) {
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .build()
            chain.proceedRevokeSessionOnError(newRequest, session)
        } else {
            val requestBody = getRefreshTokenRequestBody(session)
            val refreshRequest = originalRequest
                .newBuilder()
                .url(authUrl)
                .post(requestBody)
                .build()
            val refreshResponse = chain.proceedRevokeSessionOnError(refreshRequest, session)

            if (refreshResponse.isSuccessful) {
                val adapter = Moshi.Builder().build().adapter(Token::class.java)
                val token = adapter.fromJson(refreshResponse.body.toString())!!
                localStorageRepository.session = Session(
                    token.accessToken,
                    Instant.now().epochSecond + token.expiresIn,
                    session.refreshToken,
                    token.tokenType
                )
                val newCall = originalRequest
                    .newBuilder()
                    .addHeader("Authorization", "Bearer ${token.accessToken}")
                    .build()
                chain.proceedRevokeSessionOnError(newCall, session)
            } else chain.proceedRevokeSessionOnError(chain.request(), session)
        }
    }

    private fun Interceptor.Chain.proceedRevokeSessionOnError(
        request: Request, session: Session
    ): Response {
        val response = proceed(request)

        return if (response.code == 401) {
            localStorageRepository.session = null
            localStorageRepository.baseUrl = LocalStorageRepository.PLACEHOLDER_URL
            val requestBody = getRevokeTokenRequestBody(session)
            val revokeRequest = request.newBuilder()
                .url(authUrl)
                .post(requestBody)
                .build()
            proceed(revokeRequest)
        } else response
    }

    private fun getRefreshTokenRequestBody(session: Session): RequestBody =
        FormBody.Builder()
            .add("grant_type", AuthenticationRepository.GRANT_TYPE_REFRESH)
            .add("refresh_token", session.refreshToken)
            .add("client_id", AuthenticationRepository.CLIENT_ID)
            .build()

    private fun getRevokeTokenRequestBody(session: Session): RequestBody =
        FormBody.Builder()
            .add("token", session.accessToken)
            .add("action", AuthenticationRepository.REVOKE_ACTION)
            .build()
}