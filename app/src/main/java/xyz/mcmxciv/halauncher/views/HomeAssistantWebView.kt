package xyz.mcmxciv.halauncher.views

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import xyz.mcmxciv.halauncher.utilities.UserPreferences
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

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

    fun loadHomeAssistant(url: String) {
        loadUrl(url)

        if (UserPreferences.transparentBackground) {
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
