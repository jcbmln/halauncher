package xyz.mcmxciv.halauncher.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.HomeAssistantDiscoveryListener
import xyz.mcmxciv.halauncher.HomeAssistantResolveListener
import xyz.mcmxciv.halauncher.ServiceListAdapter
import xyz.mcmxciv.halauncher.databinding.ActivitySetupBinding
import xyz.mcmxciv.halauncher.utils.UserPreferences
import kotlin.collections.ArrayList

class SetupActivity : AppCompatActivity(),
    HomeAssistantDiscoveryListener.Callback,
    HomeAssistantResolveListener.Callback
{
    private lateinit var binding: ActivitySetupBinding
    private lateinit var adapter: ServiceListAdapter
    private lateinit var prefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = UserPreferences.getInstance(this)

//        binding.setupButtonEnter.setOnClickListener {
//            val text = binding.setupUrlInput.text.toString()
//
//            if (text.toLowerCase(Locale.ROOT).startsWith("https://") ||
//                text.toLowerCase(Locale.ROOT).startsWith("http://"))
//            {
//                prefs.url = text
//                prefs.setupDone = true
//                finish()
//            }
//            else {
//                val toast = Toast.makeText(
//                    this, "You must enter a valid URL.", Toast.LENGTH_LONG
//                )
//                toast.setPosition(binding.setupManualContainer, window, 0, 5)
//                toast.show()
//            }
//        }

        val nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager

        binding.setupServiceList.layoutManager = LinearLayoutManager(this)
        adapter = ServiceListAdapter(ArrayList(), nsdManager, this)
        binding.setupServiceList.adapter = adapter

        val discoveryListener = HomeAssistantDiscoveryListener(this, nsdManager)
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
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

    override fun addService(serviceInfo: NsdServiceInfo) {
        val handler = Handler(mainLooper)
        handler.post {
            adapter.addServiceItem(serviceInfo)
            binding.setupLoadingLayout.visibility = View.GONE
            binding.setupSelectionLayout.visibility = View.VISIBLE
        }
    }

    override fun openService(serviceInfo: NsdServiceInfo) {
        val url = "http://${serviceInfo.host.hostAddress}:${serviceInfo.port}"
        prefs.url = url
        prefs.setupDone = true

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "SetupActivity"
        const val SERVICE_TYPE = "_home-assistant._tcp"
    }
}
