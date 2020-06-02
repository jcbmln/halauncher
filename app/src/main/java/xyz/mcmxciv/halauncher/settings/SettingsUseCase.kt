package xyz.mcmxciv.halauncher.settings

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    val hasHassInstance: Boolean
        get() = settingsRepository.instanceUrl != SettingsRepository.PLACEHOLDER_URL

    fun saveInstanceUrl(url: String) {
        val instanceUrl = try {
            val httpUrl = url.toHttpUrl()
            HttpUrl.Builder()
                .scheme(httpUrl.scheme)
                .host(httpUrl.host)
                .port(httpUrl.port)
                .build()
                .toString()
        } catch (ex: IllegalArgumentException) { throw ex }

        settingsRepository.instanceUrl = instanceUrl
    }
}
