package xyz.mcmxciv.halauncher.shortcuts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.apps.AppLauncher
import xyz.mcmxciv.halauncher.databinding.ShortcutItemBinding
import xyz.mcmxciv.halauncher.utils.HassTheme
import xyz.mcmxciv.halauncher.utils.ResourceProvider

class ShortcutListAdapter(
    private val resourceProvider: ResourceProvider,
    private val appLauncher: AppLauncher,
    private val onShortcutSelectedListener: OnShortcutSelectedListener
) : ListAdapter<Shortcut, ShortcutListAdapter.ShortcutListViewHolder>(itemCallback) {

    inner class ShortcutListViewHolder(
        private val binding: ShortcutItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shortcut: Shortcut) {
            binding.shortcutIcon.leftIcon = shortcut.icon.toDrawable(resourceProvider.resources)
            binding.shortcutIcon.text = shortcut.displayName
            binding.shortcutIcon.tag = shortcut

            binding.shortcutIcon.setOnClickListener { view ->
                val item = view.tag as Shortcut
                appLauncher.startShortcut(item.packageName, item.shortcutId, view)
                onShortcutSelectedListener()
            }
            binding.shortcutIcon.setTextColor(HassTheme.instance.primaryTextColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ShortcutListViewHolder(
            ShortcutItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ShortcutListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val itemCallback = object : DiffUtil.ItemCallback<Shortcut>() {
            override fun areItemsTheSame(oldItem: Shortcut, newItem: Shortcut): Boolean =
                oldItem.shortcutId == newItem.shortcutId

            override fun areContentsTheSame(
                oldItem: Shortcut,
                newItem: Shortcut
            ): Boolean = oldItem == newItem
        }
    }
}
