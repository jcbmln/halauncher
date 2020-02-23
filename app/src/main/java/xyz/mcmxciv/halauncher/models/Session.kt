package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.JsonClass
import org.threeten.bp.Instant

@JsonClass(generateAdapter = true)
data class Session(
    val accessToken: String,
    val expirationTimestamp: Long,
    val refreshToken: String,
    val tokenType: String
) : Model() {
    val isExpired: Boolean
        get() = expirationTimestamp < Instant.now().epochSecond

    val expiresIn: Long
        get() = expirationTimestamp - Instant.now().epochSecond

    companion object : JsonModel<Session>()
}