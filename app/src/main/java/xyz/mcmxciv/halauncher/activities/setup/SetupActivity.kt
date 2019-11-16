package xyz.mcmxciv.halauncher.activities.setup

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.mcmxciv.halauncher.activities.HomeActivity
import xyz.mcmxciv.halauncher.fragments.DiscoveryFragment
import xyz.mcmxciv.halauncher.databinding.ActivitySetupBinding
import xyz.mcmxciv.halauncher.interfaces.DiscoveryServiceSelectedListener
import xyz.mcmxciv.halauncher.utils.UserPreferences

class SetupActivity : AppCompatActivity(), DiscoveryServiceSelectedListener
{
    private lateinit var binding: ActivitySetupBinding
    private lateinit var prefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = UserPreferences.getInstance(this)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(binding.setupFragmentContainer.id, DiscoveryFragment(this))
        fragmentTransaction.commit()
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
