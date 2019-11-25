package xyz.mcmxciv.halauncher.activities.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.databinding.ActivityHomeBinding
import xyz.mcmxciv.halauncher.utils.AppPreferences

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        prefs = AppPreferences.getInstance(this)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        setContentView(binding.root)
        loadWebView()

        //viewModel.getAppList(this, idp)

        binding.homeAppBar.appList.layoutManager = LinearLayoutManager(this)

        viewModel.appList.observe(this, Observer {
            binding.homeAppBar.appList.adapter = AppListAdapter(it)
        })

        binding.homeParentLayout.slidableView = binding.homeSlidableView
        binding.homeParentLayout.revealableView = binding.homeAppBar.appList
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun loadWebView() {
        binding.homeWebView.loadHomeAssistant(prefs.url)
    }

    private fun hideSystemUI() {
        // Enables sticky immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
