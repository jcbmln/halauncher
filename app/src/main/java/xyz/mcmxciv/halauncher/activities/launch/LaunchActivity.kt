package xyz.mcmxciv.halauncher.activities.launch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.activities.authentication.AuthenticationActivity
import xyz.mcmxciv.halauncher.activities.home.HomeActivity
import xyz.mcmxciv.halauncher.activities.setup.SetupActivity
import xyz.mcmxciv.halauncher.utils.AppPreferences

class LaunchActivity : AppCompatActivity() {
    private lateinit var viewModel: LaunchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LaunchViewModel::class.java)
        val prefs = AppPreferences.getInstance(this)

        if (prefs.setupDone) {
            viewModel.validateSession()
            viewModel.sessionValidated.observe(this, Observer {
                if (it) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                }

                finish()
            })
        }
        else {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
