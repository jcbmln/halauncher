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
import xyz.mcmxciv.halauncher.utilities.UserSettings
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

        if (UserSettings.transparentBackground) {
            if (UserSettings.blurBackground && UserSettings.canGetWallpaper) {
                val manager = WallpaperManager.getInstance(context)
                val wallpaper = manager.drawable

                if (manager.drawable is BitmapDrawable) {
                    BlurTask(context, wallpaper.toBitmap()).execute(this)
                }
            }

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

    private class BlurTask
        internal constructor(context: Context, bmp: Bitmap): AsyncTask<HomeAssistantWebView, Void, Bitmap>() {
        private val bitmapScale = 0.4f
        private val blurRadius = 20f
        private val contextReference: WeakReference<Context> = WeakReference(context)
        private var webViewReference: WeakReference<HomeAssistantWebView>? = null
        private var bitmap: Bitmap = bmp

        override fun doInBackground(vararg params: HomeAssistantWebView?): Bitmap {
            webViewReference = WeakReference(params[0]!!)
            val width = (bitmap.width * bitmapScale).roundToInt()
            val height = (bitmap.height * bitmapScale).roundToInt()

            val input = Bitmap.createScaledBitmap(bitmap, width, height, false)
            val output = Bitmap.createBitmap(input)

            val rs = RenderScript.create(contextReference.get())
            val intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            val tmpIn = Allocation.createFromBitmap(rs, input)
            val tmpOut = Allocation.createFromBitmap(rs, output)

            intrinsicBlur.setRadius(blurRadius)
            intrinsicBlur.setInput(tmpIn)
            intrinsicBlur.forEach(tmpOut)

            tmpOut.copyTo(output)

            return output
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            webViewReference?.get()?.background = result?.toDrawable(contextReference.get()!!.resources)
        }
    }
}
