package xyz.mcmxciv.halauncher.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import xyz.mcmxciv.halauncher.views.HomeAssistantWebView
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utilities.UserSettings

class MainActivity : Activity() {
    private val setupActivityCode: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UserSettings.init(applicationContext)
        val url = UserSettings.url

        if (url.isNullOrBlank()) {
            val intent = Intent(this, SetupActivity::class.java)
            startActivityForResult(intent, setupActivityCode)
        }
        else {
            openUrl(url)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == setupActivityCode) {
            val url = data?.getStringExtra("url")
            openUrl(url)
        }
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

    private fun openUrl(url: String?) {
        val mainWebView = findViewById<HomeAssistantWebView>(R.id.main_web_view)

        if (!url.isNullOrBlank()) {
            mainWebView.loadUrl(url)
        }
        else {
            Toast.makeText(this, "No URL was provided.", Toast.LENGTH_LONG).show()
        }
    }
}
