package xyz.mcmxciv.halauncher.domain.models

import xyz.mcmxciv.halauncher.data.models.Token

sealed class TokenResult {
    data class Success(val token: Token) : TokenResult()
    object InvalidRequest : TokenResult()
    object InactiveUser : TokenResult()
    object UnknownError : TokenResult()
}
