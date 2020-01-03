package xyz.mcmxciv.halauncher.utils

//sealed class SessionState {
//    object Validating : SessionState()
//    object Valid : SessionState()
//    object Invalid : SessionState()
//    object NewUser : SessionState()
//    data class Error(val message: String) : SessionState()
//}

enum class SessionState {
    Valid,
    Invalid,
    NewUser
}