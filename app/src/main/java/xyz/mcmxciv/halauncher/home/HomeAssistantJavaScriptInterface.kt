package xyz.mcmxciv.halauncher.home

import android.webkit.JavascriptInterface

interface HomeAssistantJavaScriptInterface {
    fun getExternalAuth(result: String)
    fun revokeExternalAuth(result: String)
    fun themesUpdated(result: String)
    fun externalBus(message: String)
}
