package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.JsonClass
import org.threeten.bp.Instant
import xyz.mcmxciv.halauncher.data.models.SerializerObject
import xyz.mcmxciv.halauncher.data.models.SerializableModel

@JsonClass(generateAdapter = true)
data class Session(
    val accessToken: String,
    val expirationTimestamp: Long,
    val refreshToken: String,
    val tokenType: String
) : SerializableModel() {
    val isExpired: Boolean
        get() = expirationTimestamp < Instant.now().epochSecond

    val expiresIn: Long
        get() = expirationTimestamp - Instant.now().epochSecond

    companion object : SerializerObject<Session>()
}