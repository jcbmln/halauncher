package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.drawable.ColorDrawable
import android.os.Process
import android.util.DisplayMetrics
import android.view.*
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.PopupWindowShortcutsBinding
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.utils.AppLauncher
import xyz.mcmxciv.halauncher.utils.Utilities
import xyz.mcmxciv.halauncher.utils.getSourceBounds


class ShortcutPopupWindow(
    private val context: Context,
    private val appListItem: AppListItem,
    private val appLauncher: AppLauncher
) : ShortcutListAdapter.ShorcutSelectedListener {
    private val binding: PopupWindowShortcutsBinding
    private val popup: PopupWindow
    private val dm = DisplayMetrics()

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PopupWindowShortcutsBinding.inflate(inflater)

        binding.appInfoText.setOnClickListener { view ->
            dismiss()
            val launcherApps = context
                .getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            launcherApps.startAppDetailsActivity(
                appListItem.componentName,
                Process.myUserHandle(),
                view.getSourceBounds(),
                null
            )
        }

        if (appListItem.isSystemApp) {
            binding.uninstallText.isEnabled = false
            binding.uninstallText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                context.getDrawable(R.drawable.ic_remove_disabled),
                null,
                null
            )
        } else {
            binding.uninstallText.setOnClickListener {
                dismiss()
                appLauncher.uninstall(appListItem.componentName, context)
            }
        }

        val shortcutItems = appListItem.shortcutItems
        if (shortcutItems != null && shortcutItems.isNotEmpty()) {
            binding.shortcutList.layoutManager = LinearLayoutManager(context)
            binding.shortcutList.adapter = ShortcutListAdapter(context, shortcutItems, this)
        } else {
            binding.shortcutList.isVisible = false
            binding.systemShortcuts.background = ColorDrawable(context.getColor(R.color.transparent))
        }

        binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        popup = PopupWindow(
            binding.root,
            binding.root.measuredWidth,
            binding.root.measuredHeight
        )
        popup.animationStyle = R.style.HaLauncherTheme_PopupAnimation
        popup.isOutsideTouchable = true
        popup.isFocusable = true
    }

    override fun onShortcutSelected() {
        dismiss()
    }

    private fun dismiss() {
        popup.dismiss()
    }

    fun show(view: View) {
        val baseXPos = (view.left + (view.width / 2)) - (popup.width / 2)
        val xPos = when {
            view.left == 0 -> view.left + Utilities.pxFromDp(10f, dm)
            baseXPos + popup.width >= dm.widthPixels ->
                dm.widthPixels - popup.width - Utilities.pxFromDp(10f, dm)
            else -> baseXPos
        }
        val yPos = when {
            (view.bottom + popup.height) > dm.heightPixels ->
                view.top + (view.height / 2) - popup.height
            else -> view.bottom
        }

        popup.showAtLocation(view, Gravity.NO_GRAVITY, xPos, yPos)
    }
}