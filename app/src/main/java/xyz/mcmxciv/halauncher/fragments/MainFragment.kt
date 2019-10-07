package xyz.mcmxciv.halauncher.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.SetupActivity
import xyz.mcmxciv.halauncher.utilities.UserPreferences
import xyz.mcmxciv.halauncher.views.HomeAssistantWebView

class MainFragment
internal constructor(drawable: Drawable?): ViewPagerFragment(drawable) {
    private val setupActivityCode: Int = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPage(UserPreferences.url)
    }

    private fun loadPage(url: String?) {
        if (url.isNullOrBlank()) {
            val intent = Intent(activity, SetupActivity::class.java)
            startActivityForResult(intent, setupActivityCode)
        }
        else {
            openUrl(url)
        }
    }

    private fun openUrl(url: String?) {
        if (!url.isNullOrBlank()) {
            val mainWebView = view?.findViewById<HomeAssistantWebView>(R.id.main_web_view)
            mainWebView?.loadHomeAssistant(url)
        }
        else {
            Toast.makeText(context, "No URL was provided.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == setupActivityCode) {
            val url = data?.getStringExtra("url")
            openUrl(url)
        }
    }

}
