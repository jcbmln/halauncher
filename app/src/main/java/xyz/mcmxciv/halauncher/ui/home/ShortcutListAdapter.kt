package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Process
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.ShortcutItemBinding
import xyz.mcmxciv.halauncher.models.apps.ShortcutItem
import xyz.mcmxciv.halauncher.utils.Utilities
import xyz.mcmxciv.halauncher.utils.getBounds

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
        val drawable = shortcutItem.icon?.toDrawable(resources)
        val dm = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        val size = Utilities.pxFromDp(32f, dm)
        drawable?.setBounds(0, 0, size, size)
        holder.binding.shortcutNameText
            .setCompoundDrawables(drawable, null, null, null)
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
                    view.getBounds(),
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