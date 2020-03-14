package xyz.mcmxciv.halauncher.ui.main

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Process
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.ShortcutItemBinding
import xyz.mcmxciv.halauncher.models.apps.ShortcutItem
import xyz.mcmxciv.halauncher.utils.getSourceBounds

class ShortcutListAdapter(
    private val context: Context,
    private val shortcutItems: List<ShortcutItem>,
    private val listener: ShorcutSelectedListener
) : RecyclerView.Adapter<ShortcutListAdapter.ShortcutListViewHolder>() {

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
            val launcherApps = context
                .getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            if (launcherApps.hasShortcutHostPermission()) {
                launcherApps.startShortcut(
                    item.packageName,
                    item.shortcutId,
                    view.getSourceBounds(),
                    null,
                    Process.myUserHandle()
                )
            }

            listener.onShortcutSelected()
        }
    }

    override fun getItemCount(): Int = shortcutItems.size

    interface ShorcutSelectedListener {
        fun onShortcutSelected()
    }
}