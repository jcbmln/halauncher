package xyz.mcmxciv.halauncher.ui.main.applist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.ListItemAppBinding
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.ui.main.shortcuts.ShortcutPopupWindow
import xyz.mcmxciv.halauncher.utils.AppLauncher
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import xyz.mcmxciv.halauncher.utils.Utilities
import javax.inject.Inject

class AppListAdapter @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val appLauncher: AppLauncher
) : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {
    private var appListItems = listOf<AppListItem>()
    private var textColor: Int = 0

    class AppListViewHolder(
        val binding: ListItemAppBinding,
        private val resourceProvider: ResourceProvider,
        private val appLauncher: AppLauncher
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        private var popup: ShortcutPopupWindow? = null
        private lateinit var appListItem: AppListItem

        init {
            binding.appItem.setOnClickListener(this)
            binding.appItem.setOnLongClickListener(this)
        }

        fun populate(item: AppListItem, textColor: Int) {
            appListItem = item
            val resources = LauncherApplication.instance.resources
            binding.appItem.topIcon = appListItem.icon.toDrawable(resources)
            binding.appItem.text = appListItem.displayName
            binding.appItem.tag = appListItem

            if (textColor != 0)
                binding.appItem.setTextColor(textColor)

        }

        override fun onClick(view: View) {
            val item = view.tag as AppListItem
            appLauncher.startMainActivity(item.componentName, view)
        }

        override fun onLongClick(view: View): Boolean {
            if (popup == null) {
                popup =
                    ShortcutPopupWindow(
                        view,
                        resourceProvider,
                        appListItem,
                        appLauncher
                    )
            }

            popup?.show()
            return true
        }
    }

    private var isDarkBackground: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppListViewHolder(
            ListItemAppBinding.inflate(inflater, parent, false),
            resourceProvider,
            appLauncher
        )
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        holder.populate(appListItems[position], textColor)
    }

    override fun getItemCount() = appListItems.size

    fun update(items: List<AppListItem>) {
        appListItems = items
        notifyDataSetChanged()
    }

    fun setTextColor(color: Int) {
        textColor = color
        notifyDataSetChanged()
    }
}