package xyz.mcmxciv.halauncher.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.databinding.ActivityHomeBinding
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.utils.UserPreferences

class HomeActivity : AppCompatActivity() {
    private val setupActivityCode: Int = 1
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UserPreferences.init(this)

        if (UserPreferences.isFirstRun) {
            val intent = Intent(this, SetupActivity::class.java)
            startActivityForResult(intent, setupActivityCode)
        }
        else {
            loadWebView()
        }

        if (UserPreferences.canGetWallpaper) {
            SetupActivity.setWallpaper(this, window)
        }

        val idp = InvariantDeviceProfile.getInstance(this)
        val appList = getAppList(this, idp)

        binding.homeAppBar.appList.layoutManager = LinearLayoutManager(this)
        binding.homeAppBar.appList.adapter = AppListAdapter(appList)

        binding.homeParentLayout.slidableView = binding.homeSlidableView
        binding.homeParentLayout.revealableView = binding.homeAppBar.appList
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            setupActivityCode -> {
                UserPreferences.isFirstRun = false
                loadWebView()
            }
        }
    }

    private fun getAppList(context: Context, idp: InvariantDeviceProfile):
            ArrayList<AppInfo> {
        val factory = IconFactory(context, idp.iconBitmapSize)
        val appList = ArrayList<AppInfo>()
        val pm = context.packageManager
        val launcherIntent = Intent().apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        pm.getInstalledApplications(0).forEach { appInfo ->
            launcherIntent.`package` = appInfo.packageName
            // only show launch-able apps
            if (pm.queryIntentActivities(launcherIntent, 0).size > 0) {
                val unbadgedIcon = appInfo.loadUnbadgedIcon(pm)
                appList.add(AppInfo().apply {
                    packageName = appInfo.packageName
                    displayName = pm.getApplicationLabel(appInfo).toString()
                    icon = factory.createIcon(unbadgedIcon)
                })
            }
        }

        appList.sortBy { item -> item.displayName }
        return appList
    }

    private fun loadWebView() {
        binding.homeWebView.loadHomeAssistant(UserPreferences.url)
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
