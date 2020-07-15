package xyz.mcmxciv.halauncher.settings

import android.util.Patterns
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.utils.HassTheme
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
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
        settingsRepository.instanceUrl = validateUrl(url)
            .toHttpUrl()
            .toString()
            .removeSuffix("/")
    }

    private fun validateUrl(url: String): String {
        var formattedUrl = url
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            formattedUrl = "https://$url"
        }

        if (!Pattern.matches(Patterns.WEB_URL.toString(), formattedUrl)) throw InvalidUrlException()

        return formattedUrl
    }
}
