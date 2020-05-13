package xyz.mcmxciv.halauncher.data.models

sealed class TokenResult {
    data class Success(val token: Token) : TokenResult()
    data class Error(val message: String) : TokenResult()
    object InvalidRequest : TokenResult()
    object InactiveUser : TokenResult()
}