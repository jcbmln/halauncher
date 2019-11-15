package xyz.mcmxciv.halauncher.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.mcmxciv.halauncher.utils.UserPreferences

class LaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = UserPreferences.getInstance(this)

        if (prefs.setupDone) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}
