package xyz.mcmxciv.halauncher.domain.models

import com.squareup.moshi.JsonClass
import org.threeten.bp.Instant
import xyz.mcmxciv.halauncher.data.models.SerializerObject
import xyz.mcmxciv.halauncher.data.models.SerializableModel
import xyz.mcmxciv.halauncher.data.models.Token

@JsonClass(generateAdapter = true)
data class Session(
    val accessToken: String,
    val expirationTimestamp: Long,
    val refreshToken: String,
    val tokenType: String
) : SerializableModel() {
    constructor(token: Token) : this(
        token.accessToken,
        Instant.now().epochSecond + token.expiresIn,
        token.refreshToken!!,
        token.tokenType
    )

    val isExpired: Boolean
        get() = expirationTimestamp < Instant.now().epochSecond

    val expiresIn: Long
        get() = expirationTimestamp - Instant.now().epochSecond

    companion object : SerializerObject<Session>()
}