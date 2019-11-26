package xyz.mcmxciv.halauncher.models

import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.utils.AppPreferences
import java.time.Instant
import kotlin.system.exitProcess

data class Session(
    val accessToken: String,
    val expirationTimestamp: Long,
    val refreshToken: String,
    val tokenType: String
) {
//    val isExpired: Boolean = (expirationTimestamp - Instant.now().epochSecond) < 0
    val isExpired: Boolean = Instant.now().epochSecond > expirationTimestamp
    val expiresIn = expirationTimestamp - Instant.now().epochSecond

    private fun save() {
        val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())
        prefs.accessToken = accessToken
        prefs.expirationTimestamp = expirationTimestamp
        prefs.refreshToken = refreshToken
        prefs.tokenType = tokenType
    }

    companion object {
        fun create(token: AuthToken): Session {
            val session = Session(
                token.accessToken,
                Instant.now().epochSecond + token.expiresIn,
                token.refreshToken!!,
                token.tokenType
            )
            session.save()
            return session
        }

        fun get(): Session? {
            val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())

            return if (prefs.accessToken != null && prefs.expirationTimestamp != -1L &&
                       prefs.refreshToken != null && prefs.tokenType != null
            ) {
                Session(
                    prefs.accessToken!!,
                    prefs.expirationTimestamp,
                    prefs.refreshToken!!,
                    prefs.tokenType!!
                )
            }
            else null
        }
    }
}