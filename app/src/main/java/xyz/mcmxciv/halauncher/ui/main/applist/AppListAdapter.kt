package xyz.mcmxciv.halauncher.ui.main.applist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.ListItemAppBinding
import xyz.mcmxciv.halauncher.models.DeviceProfile
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.ui.main.shortcuts.ShortcutPopupWindow
import xyz.mcmxciv.halauncher.utils.AppLauncher
import javax.inject.Inject

class AppListAdapter @Inject constructor(
    private val deviceProfile: DeviceProfile,
    private val appLauncher: AppLauncher
) : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {
    private var _appListItems = listOf<AppListItem>()
    private var _theme: HassTheme? = null

    var appListItems: List<AppListItem>
        get() = _appListItems
        set(value) {
            _appListItems = value
            notifyDataSetChanged()
        }

    var theme: HassTheme?
        get() = _theme
        set(value) {
            _theme = value
            notifyDataSetChanged()
        }

    class AppListViewHolder(
        val binding: ListItemAppBinding,
        private val appLauncher: AppLauncher,
        private val theme: HassTheme?,
        private val iconTextSize: Float
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {
        private lateinit var appListItem: AppListItem

        init {
            binding.appItem.setOnClickListener(this)
            binding.appItem.setOnLongClickListener(this)
        }

        fun populate(item: AppListItem) {
            appListItem = item
            val resources = LauncherApplication.instance.resources
            binding.appItem.textSize = iconTextSize
            binding.appItem.topIcon = appListItem.icon.toDrawable(resources)
            binding.appItem.text = appListItem.displayName
            binding.appItem.tag = appListItem

            theme?.let {
                binding.appItem.setTextColor(it.textPrimaryColor)
            }
        }

        override fun onClick(view: View) {
            val item = view.tag as AppListItem
            appLauncher.startMainActivity(item.componentName, view)
        }

        override fun onLongClick(view: View): Boolean {
            ShortcutPopupWindow(view, appListItem, appLauncher, theme).show()
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppListViewHolder(
            ListItemAppBinding.inflate(inflater, parent, false),
            appLauncher,
            _theme,
            deviceProfile.iconTextSize
        )
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        holder.populate(_appListItems[position])
    }

    override fun getItemCount() = _appListItems.size
}