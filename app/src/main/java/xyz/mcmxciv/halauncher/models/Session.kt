package xyz.mcmxciv.halauncher.models

import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.utils.AppPreferences
import java.time.Instant

data class Session(
    val accessToken: String,
    val expirationTimestamp: Long,
    val refreshToken: String,
    val tokenType: String
) {
    val isExpired: Boolean = (expirationTimestamp - Instant.now().epochSecond) < 0

    fun save() {
        val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())
        prefs.accessToken = accessToken
        prefs.expirationTimestamp = expirationTimestamp
        prefs.refreshToken = refreshToken
        prefs.tokenType = tokenType
    }
}