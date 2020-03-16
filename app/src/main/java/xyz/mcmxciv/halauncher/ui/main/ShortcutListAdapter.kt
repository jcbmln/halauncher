package xyz.mcmxciv.halauncher.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.ShortcutItemBinding
import xyz.mcmxciv.halauncher.models.apps.ShortcutItem
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

    class ShortcutListViewHolder(val binding: ShortcutItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ShortcutListViewHolder(
            ShortcutItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ShortcutListViewHolder, position: Int) {
        val shortcutItem = shortcutItems[position]
        val resources = LauncherApplication.instance.resources
        holder.binding.shortcutNameText.leftIcon = shortcutItem.icon.toDrawable(resources)
        holder.binding.shortcutNameText.text = shortcutItem.displayName
        holder.binding.shortcutNameText.tag = shortcutItem

        holder.binding.shortcutNameText.setOnClickListener { view ->
            val item = view.tag as ShortcutItem
            appLauncher.startShortcut(item.packageName, item.shortcutId, view)
            listener.onShortcutSelected()
        }
    }

    override fun getItemCount(): Int = shortcutItems.size

    interface ShorcutSelectedListener {
        fun onShortcutSelected()
    }
}