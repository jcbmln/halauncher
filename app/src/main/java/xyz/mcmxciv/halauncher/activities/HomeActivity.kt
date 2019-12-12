package xyz.mcmxciv.halauncher.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.mcmxciv.halauncher.databinding.HomeActivityBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
