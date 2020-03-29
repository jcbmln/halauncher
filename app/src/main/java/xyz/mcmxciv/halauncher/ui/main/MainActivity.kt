package xyz.mcmxciv.halauncher.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import xyz.mcmxciv.halauncher.ui.observe
import xyz.mcmxciv.halauncher.utils.BlurBuilder
import javax.inject.Inject

class MainActivity : AppCompatActivity(), PackageReceiver.PackageListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var packageReceiver: PackageReceiver
    private lateinit var theme: HassTheme

    @Inject
    lateinit var idp: DeviceProfile

    @Inject
    lateinit var appListAdapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LauncherApplication.instance.component.inject(this)
        viewModel = createViewModel {
            LauncherApplication.instance.component.mainActivityViewModel()
        }

        theme = HassTheme.createDefaultTheme(this)

        observe(viewModel.appListItems) { items ->
            appListAdapter.appListItems = items
        }

        observe(viewModel.config) { config ->
            config?.let { setThemeColor(Color.parseColor(it.themeColor)) }
        }

        observe(viewModel.theme) { newTheme ->
            theme = newTheme

            binding.searchInputLayout.setBoxStrokeColorStateList(newTheme.inputStateList)
            binding.searchInputLayout.defaultHintTextColor = newTheme.inputStateList
            binding.searchInputLayout.hintTextColor = newTheme.inputStateList
            binding.moreButton.drawable.setTint(newTheme.primaryTextColor)
            binding.closeButton.drawable.setTint(newTheme.primaryTextColor)
            binding.allAppsButton.backgroundTintList = ColorStateList.valueOf(newTheme.accentColor)
            val appListContainerBackground = ColorDrawable(newTheme.primaryBackgroundColor)
            appListContainerBackground.alpha = 150
            binding.appListContainer.background = appListContainerBackground
            window.statusBarColor = newTheme.primaryColor
            window.navigationBarColor = newTheme.primaryColor
            appListAdapter.theme = theme
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val x = binding.allAppsButton.left + (binding.allAppsButton.width / 2)
            val y = binding.allAppsButton.top + (binding.allAppsButton.height / 2)
            binding.appListContainer.setAnimationStartPoint(x, y)
        }

        binding.appList.layoutManager = GridLayoutManager(this, idp.appDrawerColumns)
        binding.appList.adapter = appListAdapter

        binding.allAppsButton.setOnClickListener {
            openAppList()
        }

        binding.closeButton.setOnClickListener {
            closeAppList()
        }
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
        if (binding.appListContainer.isVisible) {
            closeAppList()
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

    private fun openAppList() {
        if (!binding.appListContainer.isVisible) {
            val background = BlurBuilder.blur(binding.appNavigationHostFragment)
            binding.appListBackground.background = BitmapDrawable(
                resources,
                background
            )
            binding.appListBackground.visibility = View.VISIBLE
            binding.appListContainer.animateOpen()
            binding.allAppsButton.animateClose()
        }
    }

    private fun closeAppList() {
        if (binding.appListContainer.isVisible) {
            binding.appListBackground.visibility = View.GONE
            binding.appListContainer.animateClose()
            binding.allAppsButton.visibility = View.VISIBLE
            binding.allAppsButton.animateOpen()
        }
    }

    private fun setThemeColor(color: Int) {
        window.statusBarColor = color
        window.navigationBarColor = color
    }

    companion object {
        const val UPDATE_REQUEST_CODE = 1
    }
}
