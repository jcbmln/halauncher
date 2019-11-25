package xyz.mcmxciv.halauncher.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import xyz.mcmxciv.halauncher.activities.authentication.AuthenticationActivity
import xyz.mcmxciv.halauncher.activities.home.HomeActivity
import xyz.mcmxciv.halauncher.activities.setup.SetupActivity
import xyz.mcmxciv.halauncher.utils.AppPreferences

class LaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences.getInstance(this)

        if (prefs.setupDone) {
            if (prefs.isAuthenticated) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, AuthenticationActivity::class.java)
                startActivity(intent)
            }
        }
        else {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}
