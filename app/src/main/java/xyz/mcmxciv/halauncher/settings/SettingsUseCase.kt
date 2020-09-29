package xyz.mcmxciv.halauncher.settings

import android.util.Patterns
import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.utils.HassTheme
import java.util.regex.Pattern
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    var appDrawerColumns: Int
        get() = settingsRepository.appDrawerColumns
        set(value) { settingsRepository.appDrawerColumns = value }

    val iconColumnOptions: List<Int>
        get() = settingsRepository.iconColumnOptions

    var theme: HassTheme
        get() = settingsRepository.theme
        set(value) { settingsRepository.theme = value }

    val webviewUrl: String
        get() = settingsRepository.connectionUrl.toHttpUrl()
            .newBuilder()
            .addEncodedQueryParameter("external_auth", "1")
            .build()
            .toString()

    fun validateInstance(): Boolean =
        settingsRepository.connectionUrl != SettingsRepository.PLACEHOLDER_URL

    fun saveInstanceUrl(url: String) {
        settingsRepository.connectionUrl = validateUrl(url)
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
