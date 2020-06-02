package xyz.mcmxciv.halauncher.http

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import xyz.mcmxciv.halauncher.settings.SettingsRepository
import java.lang.IllegalStateException

class UrlInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val instanceUrl = sharedPreferences.getString(
            SettingsRepository.INSTANCE_URL_KEY,
            null
        ) ?: throw IllegalStateException()

        val url = chain.request().url.toString()
        val placeholder = SettingsRepository.PLACEHOLDER_URL

        return if (url.contains(placeholder)) {
            val newRequest = chain.request().newBuilder()
                .url(url.replace(placeholder, instanceUrl))
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(chain.request())
        }
    }
}
