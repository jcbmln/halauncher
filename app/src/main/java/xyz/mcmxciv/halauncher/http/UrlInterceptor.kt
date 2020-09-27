package xyz.mcmxciv.halauncher.http

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import java.lang.IllegalStateException
import javax.inject.Inject

class UrlInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val connectionUrl = sharedPreferences.getString(
            SettingsRepository.CONNECTION_URL_KEY,
            null
        ) ?: throw IllegalStateException()

        val url = chain.request().url.toString()
        val placeholder = SettingsRepository.PLACEHOLDER_URL

        return if (url.contains(placeholder)) {
            val newRequest = chain.request().newBuilder()
                .url(url.replace(placeholder, connectionUrl))
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(chain.request())
        }
    }
}
