package xyz.mcmxciv.halauncher.ui.home.appdrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.hadilq.liveevent.LiveEvent
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.ListItemAppBinding
import xyz.mcmxciv.halauncher.models.DeviceProfile
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.ui.home.shortcuts.ShortcutPopupWindow
import xyz.mcmxciv.halauncher.utils.AppLauncher
import javax.inject.Inject

class AppDrawerAdapter @Inject constructor(
    private val deviceProfile: DeviceProfile,
    private val appLauncher: AppLauncher
) : RecyclerView.Adapter<AppDrawerAdapter.AppListViewHolder>(),
    ShortcutPopupWindow.ShortcutActionListener {
    private var _appListItems = listOf<AppListItem>()
    var appListItems: List<AppListItem>
        get() = _appListItems
        set(value) {
            _appListItems = value
            notifyDataSetChanged()
        }

    private val _appHiddenEvent = LiveEvent<String>()
    val appHiddenEvent: LiveData<String> = _appHiddenEvent

    class AppListViewHolder(
        val binding: ListItemAppBinding,
        private val appLauncher: AppLauncher,
        private val iconTextSize: Float,
        private val listener: ShortcutPopupWindow.ShortcutActionListener
    ) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener,
        View.OnLongClickListener {
        private lateinit var _appListItem: AppListItem
        private val theme: HassTheme.AppDrawerTheme = HassTheme.instance.appDrawerTheme

        init {
            binding.appItem.setOnClickListener(this)
            binding.appItem.setOnLongClickListener(this)
        }

        fun populate(appListItem: AppListItem) {
            _appListItem = appListItem

            val resources = LauncherApplication.instance.resources
            binding.appItem.textSize = iconTextSize
            binding.appItem.topIcon = _appListItem.icon.toDrawable(resources)
            binding.appItem.text = _appListItem.displayName
            binding.appItem.tag = _appListItem
            binding.appItem.setTextColor(theme.labelTextColor)
        }

        override fun onClick(view: View) {
            val item = view.tag as AppListItem
            appLauncher.startMainActivity(item.componentName, view)
        }

        override fun onLongClick(view: View): Boolean {
            ShortcutPopupWindow(view, _appListItem, appLauncher, listener).show()
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppListViewHolder(
            ListItemAppBinding.inflate(inflater, parent, false),
            appLauncher,
            deviceProfile.iconTextSize,
            this
        )
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        holder.populate(_appListItems[position])
    }

    override fun getItemCount() = _appListItems.size

    override fun onHideActivity(activityName: String) {
        _appHiddenEvent.postValue(activityName)
    }
}
