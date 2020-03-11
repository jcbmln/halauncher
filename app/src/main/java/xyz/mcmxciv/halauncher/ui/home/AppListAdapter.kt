package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Paint
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.ListItemAppBinding
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.utils.AppLauncher
import xyz.mcmxciv.halauncher.utils.Utilities
import xyz.mcmxciv.halauncher.utils.getSourceBounds
import javax.inject.Inject

class AppListAdapter @Inject constructor(
    private val context: Context,
    private val appLauncher: AppLauncher
) : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {
    private var appListItems = listOf<AppListItem>()

    class AppListViewHolder(val binding: ListItemAppBinding)
        : RecyclerView.ViewHolder(binding.root)

    private var isDarkBackground: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppListViewHolder(ListItemAppBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appListItem = appListItems[position]
        holder.binding.root.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())
        val resources = LauncherApplication.instance.resources
        holder.binding.appItem.topIcon = appListItem.icon.toDrawable(resources)
        holder.binding.appItem.text = appListItem.displayName
        holder.binding.appItem.tag = appListItem

        if (isDarkBackground)
            holder.binding.appItem.setTextColor(context.getColor(R.color.colorBackground))

        val popup = ShortcutPopupWindow(context, appListItem, appLauncher)

        holder.binding.appItem.setOnClickListener { view ->
            val item = view.tag as AppListItem
            appLauncher.startMainActivity(item.componentName, view)
        }

        holder.binding.appItem.setOnLongClickListener {
            popup.show(it)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = appListItems.size

    fun update(items: List<AppListItem>) {
        appListItems = items
        notifyDataSetChanged()
    }

    fun setThemeColor(color: Int) {
        isDarkBackground = Utilities.isDarkColor(color)
        notifyDataSetChanged()
    }
}