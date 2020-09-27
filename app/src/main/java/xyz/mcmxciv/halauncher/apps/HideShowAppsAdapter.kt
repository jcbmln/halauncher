package xyz.mcmxciv.halauncher.apps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.HalauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.ListItemHiddenAppBinding
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class HideShowAppsAdapter @Inject constructor(
    private val resourceProvider: ResourceProvider
) : ListAdapter<AppListItem, HideShowAppsAdapter.HideShowAppsViewHolder>(
    itemCallback
) {
    private var onAppVisibilityToggledListener: OnAppVisibilityToggledListener = {}

    inner class HideShowAppsViewHolder(
        private val binding: ListItemHiddenAppBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var _appListItem: AppListItem

        fun bind(appListItem: AppListItem) {
            _appListItem = appListItem

            val resources = HalauncherApplication.instance.resources

            binding.appName.leftIcon = appListItem.icon.toDrawable(resources)
            binding.appName.text = appListItem.app.displayName
            binding.appName.tag = appListItem

            val hideDrawable = resourceProvider.getDrawable(R.drawable.ic_hide)
            val showDrawable = resourceProvider.getDrawable(R.drawable.ic_show)

            if (appListItem.app.isHidden) {
                binding.appVisibilityToggle.setImageDrawable(hideDrawable)
            }

            binding.appVisibilityToggle.setOnClickListener {
                if (appListItem.app.isHidden) {
                    binding.appVisibilityToggle.setImageDrawable(showDrawable)
                } else {
                    binding.appVisibilityToggle.setImageDrawable(hideDrawable)
                }

                onAppVisibilityToggledListener(appListItem.app.activityName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HideShowAppsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HideShowAppsViewHolder(
            ListItemHiddenAppBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HideShowAppsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnAppVisibilityToggledListener(listener: OnAppVisibilityToggledListener) {
        onAppVisibilityToggledListener = listener
    }

    companion object {
        val itemCallback = object : DiffUtil.ItemCallback<AppListItem>() {
            override fun areItemsTheSame(oldItem: AppListItem, newItem: AppListItem): Boolean =
                oldItem.app.activityName == oldItem.app.activityName

            override fun areContentsTheSame(
                oldItem: AppListItem,
                newItem: AppListItem
            ): Boolean = oldItem == newItem
        }
    }
}
