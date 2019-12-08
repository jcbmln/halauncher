package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class Token(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "expires_in")
    val expiresIn: Int,
    @Json(name = "refresh_token")
    val refreshToken: String?,
    @Json(name = "token_type")
    val tokenType: String
) {
    @Json(name = "expiration_timestamp")
    var expirationTimestamp: Long = expiresIn + Instant.now().epochSecond

    fun isExpired(): Boolean = expirationTimestamp < Instant.now().epochSecond
}