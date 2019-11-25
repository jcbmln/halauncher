package xyz.mcmxciv.halauncher.activities.integration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.databinding.ActivityIntegrationBinding

class IntegrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntegrationBinding
    private lateinit var viewModel: IntegrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntegrationBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(IntegrationViewModel::class.java)
        setContentView(binding.root)


    }
}
