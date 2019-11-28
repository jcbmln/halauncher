package xyz.mcmxciv.halauncher.activities.setup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.AppModel
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.authentication.AuthenticationActivity
import xyz.mcmxciv.halauncher.activities.home.HomeActivity
import xyz.mcmxciv.halauncher.activities.setup.discovery.DiscoveryFragment
import xyz.mcmxciv.halauncher.activities.setup.integration.IntegrationFragment
import xyz.mcmxciv.halauncher.databinding.ActivitySetupBinding
import xyz.mcmxciv.halauncher.activities.setup.manual.ManualSetupFragment
import xyz.mcmxciv.halauncher.fragments.AuthenticationFragment
import xyz.mcmxciv.halauncher.interfaces.IntegrationListener
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener
import xyz.mcmxciv.halauncher.interfaces.SetupListener
import xyz.mcmxciv.halauncher.utils.AppPreferences

class SetupActivity : AppCompatActivity(), SetupListener {
    private lateinit var binding: ActivitySetupBinding
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: SetupViewModel
    private lateinit var appModel: AppModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProviders.of(this).get(SetupViewModel::class.java)
        appModel = AppModel.getInstance(this)
        prefs = appModel.prefs

        viewModel.setupMode.observe(this, Observer {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val fragment = when (it) {
                AppModel.SetupMode.MANUAL -> ManualSetupFragment()
                else -> DiscoveryFragment()
            }

            fragmentTransaction.replace(binding.setupFragmentContainer.id, fragment)
            fragmentTransaction.commit()
        })

        binding.setupModeButton.setOnClickListener {
            when (viewModel.setupMode.value) {
                AppModel.SetupMode.MANUAL -> {
                    viewModel.setupMode.value = AppModel.SetupMode.DISCOVERY
                    binding.setupModeButton.text = getString(R.string.setup_mode_manual)
                }
                else -> {
                    viewModel.setupMode.value = AppModel.SetupMode.MANUAL
                    binding.setupModeButton.text = getString(R.string.setup_mode_discovery)
                }
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when (fragment) {
            is SetupFragment -> fragment.serviceSelectedListener = this
            is IntegrationFragment -> fragment.integrationListener = this
        }
    }

    override fun onDiscoveryModeSelected() {
        supportFragmentManager.beginTransaction()
            .replace(binding.setupFragmentContainer.id, DiscoveryFragment())
            .commit()
    }

    override fun onManualModeSelected() {
        supportFragmentManager.beginTransaction()
            .replace(binding.setupFragmentContainer.id, ManualSetupFragment())
            .commit()
    }

    override fun onServiceSelected(serviceUrl: String) {
        prefs.url = serviceUrl
        prefs.setupDone = true

        supportFragmentManager.beginTransaction()
            .replace(binding.setupFragmentContainer.id, AuthenticationFragment())
    }
//
//    override fun onIntegrationComplete() {
//        startActivity(Intent(this, HomeActivity::class.java))
//        finish()
//    }
//
//    override fun onIntegrationFailed(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }
}
