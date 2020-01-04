package xyz.mcmxciv.halauncher.api

import okhttp3.Interceptor
import okhttp3.Response
import xyz.mcmxciv.halauncher.utils.AppSettings
import xyz.mcmxciv.halauncher.utils.AuthorizationException

class TokenInterceptor constructor(
    val appSettings: AppSettings
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = appSettings.token?.accessToken ?: throw AuthorizationException()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}