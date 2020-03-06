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
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.utils.Utilities

class AppListAdapter(private val context: Context, private var appListItems: List<AppListItem>) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

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
        val drawable = appListItem.icon?.toDrawable(resources)
        holder.binding.appItem
            .setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        holder.binding.appItem.text = appListItem.displayName
        holder.binding.appItem.tag = appListItem

        if (isDarkBackground)
            holder.binding.appItem.setTextColor(context.getColor(R.color.colorBackground))

        val popup = ShortcutPopupWindow(context, appListItem.shortcutItems)

        holder.binding.appItem.setOnClickListener { view ->
            val info = view.tag as AppInfo
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(info.packageName)
            context.startActivity(intent)
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