package xyz.mcmxciv.halauncher

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = applicationContext.getSharedPreferences("UserSettings", 0)
        val url = preferences.getString("hass_url", null)

        if (url.isNullOrBlank()) {
            val editor = preferences.edit()
            val mainButtonGo = findViewById<Button>(R.id.main_button_go)

            mainButtonGo.setOnClickListener {
                val mainEditUrl = findViewById<TextView>(R.id.main_edit_url)
                val newUrl = mainEditUrl.text.toString()
                editor.putString("hass_url", newUrl)
                editor.apply()
                openUrl(newUrl)
            }
        }
        else {
            openUrl(url)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables sticky immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun openUrl(url: String) {
        val mainLayoutText = findViewById<LinearLayout>(R.id.main_layout_text)
        mainLayoutText.visibility = View.GONE

        val mainLayoutButton = findViewById<LinearLayout>(R.id.main_layout_button)
        mainLayoutButton.visibility = View.GONE

        val mainWebView = findViewById<WebView>(R.id.main_web_view)
        mainWebView.visibility = View.VISIBLE

        /*val builder = Uri.Builder()
        builder.scheme("https").authority("home.malone.xyz")
        val uri = builder.build()*/

        mainWebView.webViewClient = WebViewClient()
        mainWebView.settings.javaScriptEnabled = true
        mainWebView.settings.domStorageEnabled = true
        mainWebView.loadUrl("http://192.168.1.3:8123")
    }
}
