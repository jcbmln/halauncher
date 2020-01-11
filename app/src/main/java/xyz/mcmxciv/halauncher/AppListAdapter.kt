package xyz.mcmxciv.halauncher

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.AdaptiveIconDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.models.AppInfo

class AppListAdapter(private val appList: List<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_list_item, parent, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = appList[position]
        val appItem = holder.view.findViewById<TextView>(R.id.app_item)
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())
        val resources = LauncherApplication.instance.resources
        val drawable = appInfo.icon.toDrawable(resources)
        appItem.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        appItem.text = appInfo.displayName

        appItem.setOnClickListener {
            val context = LauncherApplication.instance.applicationContext
            val pm = context.packageManager
            context.startActivity(pm.getLaunchIntentForPackage(appInfo.packageName))
        }
    }

    override fun getItemCount() = appList.size
}