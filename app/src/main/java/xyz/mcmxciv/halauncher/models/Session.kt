package xyz.mcmxciv.halauncher.models

//import xyz.mcmxciv.halauncher.LauncherApplication
//import xyz.mcmxciv.halauncher.utils.AppPreferences
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey @ColumnInfo(name = "refresh_token") val refreshToken: String,
    @ColumnInfo(name = "access_token") val accessToken: String,
    @ColumnInfo(name = "expiration_timestamp") val expirationTimestamp: Long,
    @ColumnInfo(name = "token_type") val tokenType: String
) {
//    val isExpired: Boolean = (expirationTimestamp - Instant.now().epochSecond) < 0
    fun isExpired(): Boolean = Instant.now().epochSecond > expirationTimestamp
    fun expiresIn(): Long = expirationTimestamp - Instant.now().epochSecond

//    private fun save() {
//        val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())
//        prefs.accessToken = accessToken
//        prefs.expirationTimestamp = expirationTimestamp
//        prefs.refreshToken = refreshToken
//        prefs.tokenType = tokenType
//    }
//
//    companion object {
//        fun create(token: AuthToken): Session {
//            val session = Session(
//                token.accessToken,
//                Instant.now().epochSecond + token.expiresIn,
//                token.refreshToken!!,
//                token.tokenType
//            )
//            session.save()
//            return session
//        }
//
//        fun get(): Session? {
//            val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())
//
//            return if (prefs.accessToken != null && prefs.expirationTimestamp != -1L &&
//                       prefs.refreshToken != null && prefs.tokenType != null
//            ) {
//                Session(
//                    prefs.accessToken!!,
//                    prefs.expirationTimestamp,
//                    prefs.refreshToken!!,
//                    prefs.tokenType!!
//                )
//            }
//            else null
//        }
//    }
}