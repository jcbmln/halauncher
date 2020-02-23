package xyz.mcmxciv.halauncher.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Token(
    val accessToken: String,
    val expiresIn: Int,
    val refreshToken: String?,
    val tokenType: String
) : Model() {
    companion object : JsonModel<Token>()
}