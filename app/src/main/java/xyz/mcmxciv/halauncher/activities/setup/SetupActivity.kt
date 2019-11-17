package xyz.mcmxciv.halauncher.activities.setup

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.AppModel
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.HomeActivity
import xyz.mcmxciv.halauncher.fragments.DiscoveryFragment
import xyz.mcmxciv.halauncher.databinding.ActivitySetupBinding
import xyz.mcmxciv.halauncher.fragments.ManualSetupFragment
import xyz.mcmxciv.halauncher.utils.UserPreferences

class SetupActivity : AppCompatActivity(), DiscoveryFragment.ServiceSelectedListener {
    private lateinit var binding: ActivitySetupBinding
    private lateinit var prefs: UserPreferences
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

            when (it) {
                AppModel.SetupMode.MANUAL -> {
                    fragmentTransaction.replace(
                        binding.setupFragmentContainer.id, ManualSetupFragment()
                    )
                }
                else -> {
                    fragmentTransaction.replace(
                        binding.setupFragmentContainer.id, DiscoveryFragment()
                    )
                }
            }

            fragmentTransaction.commit()
        })

        binding.setupModeButton.setOnClickListener {
            when (viewModel.setupMode.value) {
                AppModel.SetupMode.MANUAL -> {
                    viewModel.setupMode.value = AppModel.SetupMode.DISCOVERY
                    binding.setupModeButton.text = getString(R.string.setup_mode_discovery)
                }
                else -> {
                    viewModel.setupMode.value = AppModel.SetupMode.MANUAL
                    binding.setupModeButton.text = getString(R.string.setup_mode_manual)
                }
            }
        }

        if (viewModel.setupMode.value == null) {
            viewModel.setupMode.value = AppModel.SetupMode.DISCOVERY
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment is DiscoveryFragment) {
            fragment.setServiceSelectedListener(this)
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
                prefs.canGetWallpaper = (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    override fun onServiceSelected(serviceUrl: String) {
        prefs.url = serviceUrl
        prefs.setupDone = true

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
