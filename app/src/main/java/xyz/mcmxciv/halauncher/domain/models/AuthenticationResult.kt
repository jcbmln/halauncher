package xyz.mcmxciv.halauncher.domain.models

sealed class AuthenticationResult {
    object Success : AuthenticationResult()
    object InvalidRequest : AuthenticationResult()
    object InactiveUser : AuthenticationResult()
    object UnknownError : AuthenticationResult()
}