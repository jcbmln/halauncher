package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.ListItemAppBinding
import xyz.mcmxciv.halauncher.models.apps.AppInfo
import xyz.mcmxciv.halauncher.utils.Utilities

class AppListAdapter(private val context: Context, appList: List<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val binding: ListItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    private var isDarkBackground: Boolean = false
    private var activities = appList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppListViewHolder(ListItemAppBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = activities[position]
        holder.binding.root.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())
        val resources = LauncherApplication.instance.resources
        val drawable = appInfo.icon?.toDrawable(resources)
        holder.binding.appItem
            .setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        holder.binding.appItem.text = appInfo.displayName
        holder.binding.appItem.tag = appInfo

        if (isDarkBackground)
            holder.binding.appItem.setTextColor(context.getColor(R.color.colorBackground))

        holder.binding.appItem.setOnClickListener { view ->
            val info = view.tag as AppInfo
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(info.packageName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = activities.size

    fun update(appList: List<AppInfo>) {
        activities = appList
        notifyDataSetChanged()
    }

    fun setThemeColor(color: Int) {
        isDarkBackground = Utilities.isDarkColor(color)
        notifyDataSetChanged()
    }
}