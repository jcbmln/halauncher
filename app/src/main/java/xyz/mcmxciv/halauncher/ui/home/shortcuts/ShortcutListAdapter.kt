package xyz.mcmxciv.halauncher.ui.home.shortcuts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.ShortcutItemBinding
import xyz.mcmxciv.halauncher.models.apps.ShortcutItem
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.utils.AppLauncher
import javax.inject.Inject

class ShortcutListAdapter(
    private val shortcutItems: List<ShortcutItem>,
    private val listener: ShorcutSelectedListener
) : RecyclerView.Adapter<ShortcutListAdapter.ShortcutListViewHolder>() {
    @Inject
    lateinit var appLauncher: AppLauncher

    init {
        LauncherApplication.instance.component.inject(this)
    }

    class ShortcutListViewHolder(
        val binding: ShortcutItemBinding,
        private val appLauncher: AppLauncher,
        private val listener: ShorcutSelectedListener
    ) : RecyclerView.ViewHolder(binding.root) {
        private val theme = HassTheme.instance

        fun populate(shortcutItem: ShortcutItem) {
            val resources = LauncherApplication.instance.resources
            binding.shortcutNameText.leftIcon = shortcutItem.icon.toDrawable(resources)
            binding.shortcutNameText.text = shortcutItem.displayName
            binding.shortcutNameText.tag = shortcutItem

            binding.shortcutNameText.setOnClickListener { view ->
                val item = view.tag as ShortcutItem
                appLauncher.startShortcut(item.packageName, item.shortcutId, view)
                listener.onShortcutSelected()
            }

            binding.shortcutNameText.setTextColor(theme.primaryTextColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ShortcutListViewHolder(
            ShortcutItemBinding.inflate(inflater, parent, false),
            appLauncher,
            listener
        )
    }

    override fun onBindViewHolder(holder: ShortcutListViewHolder, position: Int) {
        holder.populate(shortcutItems[position])
    }

    override fun getItemCount(): Int = shortcutItems.size

    interface ShorcutSelectedListener {
        fun onShortcutSelected()
    }
}
