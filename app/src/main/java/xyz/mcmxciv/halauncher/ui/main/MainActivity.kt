package xyz.mcmxciv.halauncher.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.play.core.install.model.ActivityResult
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.background.PackageReceiver
import xyz.mcmxciv.halauncher.databinding.ActivityMainBinding
import xyz.mcmxciv.halauncher.models.DeviceProfile
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.main.applist.AppListAdapter
import xyz.mcmxciv.halauncher.ui.main.shortcuts.ShortcutPopupWindow
import xyz.mcmxciv.halauncher.ui.observe
import xyz.mcmxciv.halauncher.utils.AppLauncher
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    PackageReceiver.PackageListener,
    ShortcutPopupWindow.ShortcutActionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var packageReceiver: PackageReceiver
    private lateinit var theme: HassTheme
    private lateinit var appListAdapter: AppListAdapter

    @Inject
    lateinit var deviceProfile: DeviceProfile

    @Inject
    lateinit var appLauncher: AppLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LauncherApplication.instance.component.inject(this)
        viewModel = createViewModel {
            LauncherApplication.instance.component.mainActivityViewModel()
        }

        theme = HassTheme.createDefaultTheme(this)
        appListAdapter = AppListAdapter(deviceProfile, appLauncher, this)

        observe(viewModel.appListItems) { items ->
            appListAdapter.appListItems = items
        }

        observe(viewModel.config) { config ->
            config?.let { setThemeColor(Color.parseColor(it.themeColor)) }
        }

        observe(viewModel.theme) { newTheme ->
            theme = newTheme

            binding.appListContainer.background = newTheme.appListBackground
            window.statusBarColor = newTheme.primaryColor
            window.navigationBarColor = newTheme.primaryColor
//            appListAdapter.theme = theme
        }

        binding.appList.layoutManager = GridLayoutManager(this, deviceProfile.appDrawerColumns)
        binding.appList.adapter = appListAdapter
    }

    override fun onResume() {
        super.onResume()
        packageReceiver = PackageReceiver.initialize(this)
        registerReceiver(packageReceiver, packageReceiver.filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(packageReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_REQUEST_CODE) {
            when (resultCode) {
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Toast
                        .makeText(this, "Failed to update app.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.mainLayout.currentState == R.id.end) {
            binding.mainLayout.transitionToStart()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.app_navigation_host_fragment)

        if (navController.navigateUp()) {
            return true
        }

        return super.onSupportNavigateUp()
    }

    override fun onPackageReceived() {
        viewModel.updateAppListItems()
    }

    override fun onHideActivity(activityName: String) {
        viewModel.hideActivity(activityName)
    }

    private fun setThemeColor(color: Int) {
        window.statusBarColor = color
        window.navigationBarColor = color
    }

    companion object {
        const val UPDATE_REQUEST_CODE = 1
    }
}
