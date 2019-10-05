package xyz.mcmxciv.halauncher.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.fragments.MainFragment
import xyz.mcmxciv.halauncher.utilities.UserSettings
import xyz.mcmxciv.halauncher.utilities.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UserSettings.init(applicationContext)

        viewPager = findViewById(R.id.main_view_pager)
        if (viewPager != null) {
            viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewPager?.adapter = viewPagerAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        val currentItem = viewPager?.currentItem
        viewPager?.adapter = viewPagerAdapter
        viewPager?.currentItem = currentItem!!
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
}
