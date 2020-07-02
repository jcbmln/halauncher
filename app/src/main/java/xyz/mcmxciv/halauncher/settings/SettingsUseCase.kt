package xyz.mcmxciv.halauncher.settings

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.utils.HassTheme
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    var theme: HassTheme
        get() = settingsRepository.theme
        set(value) { settingsRepository.theme = value }

    val webviewUrl: String
        get() = settingsRepository.instanceUrl.toHttpUrl()
            .newBuilder()
            .addEncodedQueryParameter("external_auth", "1")
            .build()
            .toString()

    fun validateInstance(): Boolean =
        settingsRepository.instanceUrl != SettingsRepository.PLACEHOLDER_URL

    fun saveInstanceUrl(url: String) {
        val instanceUrl = try {
            val httpUrl = url.toHttpUrl()
            HttpUrl.Builder()
                .scheme(httpUrl.scheme)
                .host(httpUrl.host)
                .port(httpUrl.port)
                .build()
                .toString()
                .removeSuffix("/")
        } catch (ex: IllegalArgumentException) { throw ex }

        settingsRepository.instanceUrl = instanceUrl
    }
}
