package xyz.mcmxciv.halauncher.activities

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.setPadding
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.ActivitySetupBinding
import xyz.mcmxciv.halauncher.extensions.setPosition
import xyz.mcmxciv.halauncher.utils.BlurBuilder
import xyz.mcmxciv.halauncher.utils.UserPreferences
import xyz.mcmxciv.halauncher.utils.Utilities
import java.util.*

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isReadStoragePermissionGranted()) {
            setWallpaper(this, window)
        }

        binding.setupButtonEnter.setOnClickListener {
            val text = binding.setupUrlInput.text.toString()

            if (text.toLowerCase(Locale.ROOT).startsWith("https://") ||
                text.toLowerCase(Locale.ROOT).startsWith("http://"))
            {
                UserPreferences.url = text
                UserPreferences.isFirstRun = false
                finish()
            }
            else {
                val toast = Toast.makeText(
                    this, "You must enter a valid URL.", Toast.LENGTH_LONG
                )
                toast.setPosition(binding.setupMainView, window, 0, 5)
                toast.show()
            }
        }
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

        setWallpaper(this, window)
    }

    private fun isReadStoragePermissionGranted(): Boolean {
        val permissionStatus = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )
            return false
        }

        UserPreferences.canGetWallpaper = true
        return true
    }

    companion object {
        fun setWallpaper(context: Context, window: Window) {
            if (UserPreferences.canGetWallpaper) {
                val wm = WallpaperManager.getInstance(context)
                val wallpaper = BlurBuilder.blur(context, wm.drawable)
                window.setBackgroundDrawable(wallpaper)
                UserPreferences.transparentBackground = Utilities.isDark(wallpaper)
            }
        }
    }
}
