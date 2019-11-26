package xyz.mcmxciv.halauncher.activities.integration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.activities.home.HomeActivity
import xyz.mcmxciv.halauncher.databinding.ActivityIntegrationBinding

class IntegrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntegrationBinding
    private lateinit var viewModel: IntegrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntegrationBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(IntegrationViewModel::class.java)
        setContentView(binding.root)

        viewModel.registerDevice()

        viewModel.integrationSuccess.observe(this, Observer {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        })

        viewModel.integrationError.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
    }
}
