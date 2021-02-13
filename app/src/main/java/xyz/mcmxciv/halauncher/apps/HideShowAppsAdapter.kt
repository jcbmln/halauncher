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
) : ListAdapter<App, HideShowAppsAdapter.HideShowAppsViewHolder>(
    itemCallback
) {
    private var onAppVisibilityToggledListener: OnAppVisibilityToggledListener = {}

    inner class HideShowAppsViewHolder(
        private val binding: ListItemHiddenAppBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var _app: App

        fun bind(app: App) {
            _app = app

            val resources = HalauncherApplication.instance.resources

            binding.appName.leftIcon = app.icon.toDrawable(resources)
            binding.appName.text = app.displayName
            binding.appName.tag = app

            val hideDrawable = resourceProvider.getDrawable(R.drawable.ic_hide)
            val showDrawable = resourceProvider.getDrawable(R.drawable.ic_show)

            if (app.appCacheInfo.isHidden) {
                binding.appVisibilityToggle.setImageDrawable(hideDrawable)
            }

            binding.appVisibilityToggle.setOnClickListener {
                if (app.appCacheInfo.isHidden) {
                    binding.appVisibilityToggle.setImageDrawable(showDrawable)
                } else {
                    binding.appVisibilityToggle.setImageDrawable(hideDrawable)
                }

                onAppVisibilityToggledListener(app.appCacheInfo.activityName)
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
        val itemCallback = object : DiffUtil.ItemCallback<App>() {
            override fun areItemsTheSame(oldItem: App, newItem: App): Boolean =
                oldItem.appCacheInfo.activityName == oldItem.appCacheInfo.activityName

            override fun areContentsTheSame(
                oldItem: App,
                newItem: App
            ): Boolean = oldItem == newItem
        }
    }
}
