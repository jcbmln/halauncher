package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.models.ActivityInfo

class AppListAdapter(private val context: Context, private val activityList: List<ActivityInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_app, parent, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = activityList[position]
        val appItem = holder.view.findViewById<TextView>(R.id.app_item)
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())
        val resources = LauncherApplication.instance.resources
        val drawable = appInfo.icon.toDrawable(resources)
        appItem.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        appItem.text = appInfo.displayName

        appItem.setOnClickListener {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(appInfo.packageName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = activityList.size
}