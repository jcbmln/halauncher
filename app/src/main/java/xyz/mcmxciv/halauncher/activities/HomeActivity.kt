package xyz.mcmxciv.halauncher.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.mcmxciv.halauncher.databinding.HomeActivityBinding
import xyz.mcmxciv.halauncher.utils.AppPreferences

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
