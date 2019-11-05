package xyz.mcmxciv.halauncher.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.ActivityHomeBinding
import xyz.mcmxciv.halauncher.models.AppInfo

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val invariantDeviceProfile = InvariantDeviceProfile.getInstance(this)
        val appList = getAppList(this, invariantDeviceProfile)

        binding.iconView.layoutManager = LinearLayoutManager(this)
        binding.iconView.adapter = AppListAdapter(this, appList, invariantDeviceProfile)
    }

    private fun getAppList(context: Context, invariantDeviceProfile: InvariantDeviceProfile):
            ArrayList<AppInfo> {
        val appList = ArrayList<AppInfo>()
//        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
//        val activityList = launcherApps.getActivityList(null, Process.myUserHandle())
//        val packageManager = context.packageManager
//        val iconDpi = invariantDeviceProfile.fillResIconDpi
//        val bitmapSize = invariantDeviceProfile.iconBitmapSize
//
//        for (item: LauncherActivityInfo in activityList) {
//            appList.add(AppInfo().apply {
//                packageName = item.applicationInfo.packageName
//                displayName = packageManager.getApplicationLabel(item.applicationInfo)
//                    .toString()
//                icon = IconFactory(context, iconDpi, bitmapSize, true).createBadgedIconBitmap(
//                    packageManager.getApplicationIcon(packageName), true, null
//                )
//            })
//        }
        val pm = context.packageManager
        val launcherIntent = Intent().apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        pm.getInstalledApplications(0).forEach { appInfo ->
            launcherIntent.`package` = appInfo.packageName
            // only show launch-able apps
            if (pm.queryIntentActivities(launcherIntent, 0).size > 0) {
                val appIcon = appInfo.loadUnbadgedIcon(pm)
                if (appIcon is AdaptiveIconDrawable) {
                    appList.add(AppInfo().apply {
                        packageName = appInfo.packageName
                        displayName = appInfo.processName
                        icon = appIcon
                    })
                }
            }
        }

        return appList
    }
}
