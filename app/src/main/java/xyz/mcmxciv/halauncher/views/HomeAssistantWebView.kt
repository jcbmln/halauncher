package xyz.mcmxciv.halauncher.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class HomeAssistantWebView : WebView {
    private val hassWebTitle = "Home Assistant"

    constructor(context: Context) : super(context) {
        initializeWebView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeWebView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebView() {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        webViewClient = WebViewClient()
        webChromeClient = WebChromeClient()
    }
}
