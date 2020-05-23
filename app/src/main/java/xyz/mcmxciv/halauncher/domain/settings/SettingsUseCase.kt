package xyz.mcmxciv.halauncher.domain.settings

import okhttp3.HttpUrl.Companion.toHttpUrl
import xyz.mcmxciv.halauncher.data.cache.LocalCache
import xyz.mcmxciv.halauncher.ui.HassTheme
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val localCache: LocalCache
) {
    val webviewUrl: String
        get() = localCache.instanceUrl.toHttpUrl()
            .newBuilder()
            .addEncodedQueryParameter("external_auth", "1")
            .build()
            .toString()

    var theme: HassTheme?
        get() = localCache.theme
        set(value) { localCache.theme = value }

    fun saveInstanceUrl(url: String) {
        localCache.instanceUrl = if (isAbsoluteUrl(url)) url
                else url.toHttpUrl().toString()
    }

    private fun isAbsoluteUrl(url: String): Boolean =
        Regex("^https?://").containsMatchIn(url)
}
