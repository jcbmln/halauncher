package xyz.mcmxciv.halauncher.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import xyz.mcmxciv.halauncher.utils.AppPreferences

class HomeAssistantWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {
    private val hassWebTitle = "Home Assistant"
    private val prefs = AppPreferences.getInstance(context)

    init {
        initializeWebView()
        clipToOutline = true
    }

    fun loadHomeAssistant(url: String) {
        loadUrl(url)

        if (prefs.transparentBackground) {
            setBackgroundColor(Color.argb(1, 255, 255, 255))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebView() {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        webViewClient = WebViewClient()
        webChromeClient = WebChromeClient()
    }
}