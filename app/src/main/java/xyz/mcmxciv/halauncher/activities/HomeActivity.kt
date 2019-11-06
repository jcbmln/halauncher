package xyz.mcmxciv.halauncher.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.databinding.ActivityHomeBinding
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.icons.IconShape
import xyz.mcmxciv.halauncher.models.AppInfo

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idp = InvariantDeviceProfile.getInstance(this)
        IconShape.setShape(IconShape.ShapeType.TearDrop, idp.iconBitmapSize.toFloat() / 2)
        val appList = getAppList(this, idp)

        binding.iconView.layoutManager = LinearLayoutManager(this)
        binding.iconView.adapter = AppListAdapter(this, appList, idp)
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
                    displayName = appInfo.processName
                    icon = factory.createIcon(unbadgedIcon)
                })
            }
        }

        return appList
    }
}
