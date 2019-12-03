package xyz.mcmxciv.halauncher

import android.graphics.Paint
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.AppInfo
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.views.AdaptiveIconView

class AppListAdapter(private val appList: List<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_view_item, parent, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = appList[position]
        val appImage = holder.view.findViewById<AdaptiveIconView>(R.id.app_image)
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())
        appImage.setIcon(appInfo.icon as AdaptiveIconDrawable)
        appImage.setText(appInfo.displayName)

        appImage.setOnClickListener {
            val context = LauncherApplication.getAppContext()
            val pm = context.packageManager
            context.startActivity(pm.getLaunchIntentForPackage(appInfo.packageName))
        }
    }

    override fun getItemCount() = appList.size
}