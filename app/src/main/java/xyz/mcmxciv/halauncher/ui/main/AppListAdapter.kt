package xyz.mcmxciv.halauncher.ui.main

import android.content.Context
import android.graphics.drawable.BitmapDrawable
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
import javax.inject.Inject

class AppListAdapter @Inject constructor(
    private val context: Context,
    private val appLauncher: AppLauncher
) : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {
    private var appListItems = listOf<AppListItem>()

    class AppListViewHolder(
        val binding: ListItemAppBinding,
        private val context: Context,
        private val appLauncher: AppLauncher
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        private var popup: ShortcutPopupWindow? = null
        private lateinit var appListItem: AppListItem

        init {
            binding.appItem.setOnClickListener(this)
            binding.appItem.setOnLongClickListener(this)
        }

        fun populate(item: AppListItem, isDarkBackground: Boolean) {
            appListItem = item
            val resources = LauncherApplication.instance.resources
            binding.appItem.topIcon = appListItem.icon.toDrawable(resources)
            binding.appItem.text = appListItem.displayName
            binding.appItem.tag = appListItem

            if (isDarkBackground)
                binding.appItem.setTextColor(context.getColor(R.color.colorBackground))

        }

        override fun onClick(view: View) {
            val item = view.tag as AppListItem
            appLauncher.startMainActivity(item.componentName, view)
        }

        override fun onLongClick(view: View): Boolean {
            if (popup == null) {
                popup = ShortcutPopupWindow(view, context, appListItem, appLauncher)
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
            context,
            appLauncher
        )
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
//        val appListItem = appListItems[position]
        holder.populate(appListItems[position], isDarkBackground)
//        holder.binding.root.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())


//        holder.binding.appItem.setOnClickListener { view ->
//            val item = view.tag as AppListItem
//            appLauncher.startMainActivity(item.componentName, view)
//        }

//        holder.binding.appItem.setOnLongClickListener {
//            holder.popup.show()
//            return@setOnLongClickListener true
//        }
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