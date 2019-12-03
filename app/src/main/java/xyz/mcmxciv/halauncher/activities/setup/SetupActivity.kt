package xyz.mcmxciv.halauncher.activities.setup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.databinding.ActivitySetupBinding
import xyz.mcmxciv.halauncher.utils.AppPreferences

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()

        if (AppPreferences.getInstance(this).setupDone) {
            finish()
        }
    }
}
