package xyz.mcmxciv.halauncher.data

import okhttp3.Interceptor
import okhttp3.Response
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository

class UrlInterceptor(private val baseUrl: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.toString()
        val placeholder = LocalStorageRepository.PLACEHOLDER_URL

        return if (url.contains(placeholder)) {
            val newRequest = chain.request().newBuilder()
                .url(url.replace(placeholder, baseUrl))
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(chain.request())
        }
    }
}