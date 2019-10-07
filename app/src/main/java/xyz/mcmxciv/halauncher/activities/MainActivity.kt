package xyz.mcmxciv.halauncher.activities

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utilities.UserPreferences
import xyz.mcmxciv.halauncher.utilities.ViewPagerAdapter
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UserPreferences.init(applicationContext)

        if (isReadStoragePermissionGranted()) {
            loadViewPager()
        }
    }

    override fun onResume() {
        super.onResume()
        loadViewPager()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                UserPreferences.canGetWallpaper = (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }

        loadViewPager()
    }

    private fun loadViewPager(isReloading: Boolean = false) {
        val viewPager = findViewById<ViewPager>(R.id.main_view_pager)
        if (viewPager != null) {
            val wallpaper: Drawable? = if (UserPreferences.canGetWallpaper)
                WallpaperManager.getInstance(this).drawable else null

            viewPager.background = wallpaper

            val currentItem = viewPager.currentItem
            viewPager.adapter = ViewPagerAdapter(supportFragmentManager, wallpaper)
            viewPager.currentItem = currentItem

            if (!isReloading) {
                viewPager.setPageTransformer(false) { view: View, position: Float ->
                    view.apply {
                        when {
                            position < -1 || position >= 1 -> {
                                translationX = width * position
                                alpha = 0f
                            }
                            position == 0f -> {
                                translationX = width * position
                                alpha = 1f
                            }
                            else -> {
                                translationX = width * -position
                                alpha = 1f - abs(position)
                            }
                        }
                    }
                }
            }
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

    private fun isReadStoragePermissionGranted(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return false
        }

        UserPreferences.canGetWallpaper = true
        return true
    }
}
