package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
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

    companion object {
        fun fromJson(json: String): Token? {
            val adapter = Moshi.Builder().build().adapter(Token::class.java)
            return adapter.fromJson(json)
        }

        fun toJson(token: Token): String {
            val adapter = Moshi.Builder().build().adapter(Token::class.java)
            return adapter.toJson(token)
        }
    }
}