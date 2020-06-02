package xyz.mcmxciv.halauncher.authentication

import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class Session(
    val accessToken: String,
    val expirationTimestamp: Long,
    val refreshToken: String,
    val tokenType: String
) {
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
}
