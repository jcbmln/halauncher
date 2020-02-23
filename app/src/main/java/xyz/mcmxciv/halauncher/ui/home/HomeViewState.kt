package xyz.mcmxciv.halauncher.ui.home

import xyz.mcmxciv.halauncher.models.ActivityInfo

data class HomeViewState(
    val isLoading: Boolean = false,
    val error: Error = Error.None,
    val url: String? = null,
    val callback: String? = null,
    val activities: List<ActivityInfo> = emptyList()
) {
    sealed class Error {
        object AuthenticationRevoked : Error()
        object NoAppsFound : Error()
        object WebViewError : Error()
        object None : Error()
    }
}