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
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.play.core.install.model.ActivityResult
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.background.PackageReceiver
import xyz.mcmxciv.halauncher.databinding.ActivityMainBinding
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.main.applist.AppListAdapter
import xyz.mcmxciv.halauncher.ui.observe
import xyz.mcmxciv.halauncher.utils.BlurBuilder
import xyz.mcmxciv.halauncher.utils.Utilities
import javax.inject.Inject


class MainActivity : AppCompatActivity(), PackageReceiver.PackageListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var packageReceiver: PackageReceiver
    private var palette: Palette? = null
    private var themeColor: Int = 0
    private var accentColor: Int = 0

    @Inject
    lateinit var idp: InvariantDeviceProfile

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

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val x = binding.allAppsButton.left + (binding.allAppsButton.width / 2)
            val y = binding.allAppsButton.top + (binding.allAppsButton.height / 2)
            binding.appListContainer.setAnimationStartPoint(x, y)
        }

        binding.appList.layoutManager = GridLayoutManager(this, idp.numColumns)
        binding.appList.adapter = appListAdapter

        observe(viewModel.appListItems) { items ->
            appListAdapter.update(items)
        }

        observe(viewModel.config) { config ->
            config?.let { setThemeColor(Color.parseColor(it.themeColor)) }
        }

        observe(viewModel.theme) { theme ->
            themeColor = theme.primaryColor
            accentColor = theme.accentColor
            binding.allAppsButton.backgroundTintList = ColorStateList.valueOf(accentColor)
        }

        binding.allAppsButton.setOnClickListener {
            openAppList()
        }

        binding.closeButton.setOnClickListener {
            closeAppList()
        }

        themeColor = getColor(R.color.colorAccent)
        accentColor = getColor(R.color.colorAccent)
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

    private fun generatePalette() {
        val bitmap = BlurBuilder.blur(binding.appNavigationHostFragment)
        Palette.from(bitmap).generate {
            palette = it

            it?.dominantSwatch?.let { swatch ->
                val states = arrayOf(
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf()
                )

                val colors = intArrayOf(
                    accentColor,
                    accentColor,
                    accentColor,
                    swatch.bodyTextColor
                )
                val stateList = ColorStateList(states, colors)
                binding.searchInputLayout.setBoxStrokeColorStateList(stateList)
                binding.searchInputLayout.defaultHintTextColor = stateList
                binding.moreButton.drawable.setTint(swatch.bodyTextColor)
                binding.closeButton.drawable.setTint(swatch.bodyTextColor)
                appListAdapter.setTextColor(swatch.bodyTextColor)
            }
        }
    }

    private fun openAppList() {
        generatePalette()
        if (!binding.appListContainer.isVisible) {
            val background = BlurBuilder.blur(binding.appNavigationHostFragment)
            binding.appListBackground.background = BitmapDrawable(
                resources,
                background
            )
            binding.appListBackground.visibility = View.VISIBLE
            binding.appListContainer.background = ColorDrawable(
                Utilities.createColorFromBitmap(background, themeColor, 0.75f)
            )
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
            window.statusBarColor = themeColor
            window.navigationBarColor = themeColor
        }
    }

    private fun setThemeColor(color: Int) {
        themeColor = color
        window.statusBarColor = color
        window.navigationBarColor = color
    }

    companion object {
        const val UPDATE_REQUEST_CODE = 1
    }
}
