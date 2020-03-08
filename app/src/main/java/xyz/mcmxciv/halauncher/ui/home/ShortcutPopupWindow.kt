package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageInstaller
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Process
import android.util.DisplayMetrics
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.PopupWindowShortcutsBinding
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.models.apps.ShortcutItem
import xyz.mcmxciv.halauncher.ui.MainActivity
import xyz.mcmxciv.halauncher.utils.Utilities
import xyz.mcmxciv.halauncher.utils.getBounds

class ShortcutPopupWindow(
    context: Context,
    appListItem: AppListItem
) : ShortcutListAdapter.ShorcutSelectedListener {
    private val binding: PopupWindowShortcutsBinding
    private val popup: PopupWindow
    private val dm = DisplayMetrics()

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)

        val launcherApps = context
            .getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PopupWindowShortcutsBinding.inflate(inflater)

        binding.appInfoText.setOnClickListener { view ->
            launcherApps.startAppDetailsActivity(
                appListItem.componentName,
                Process.myUserHandle(),
                view.getBounds(),
                null
            )
        }


        if (appListItem.componentName == null) {
            binding.uninstallText.isEnabled = false
        } else {
            val cn = appListItem.componentName!!
            binding.uninstallText.setOnClickListener {
                val intent = Intent(Intent.ACTION_DELETE)
                    .setData(Uri.fromParts("package", cn.packageName, cn.className))
                    .putExtra(Intent.EXTRA_USER, Process.myUserHandle())
                MainActivity.instance?.startActivity(intent)
            }
        }

        val shortcutItems = appListItem.shortcutItems
        if (shortcutItems != null && shortcutItems.isNotEmpty()) {
            binding.shortcutList.layoutManager = LinearLayoutManager(context)
            binding.shortcutList.adapter = ShortcutListAdapter(context, shortcutItems, this)
        } else {
            binding.shortcutList.isVisible = false
            binding.systemShotcuts.background = ColorDrawable(context.getColor(R.color.transparent))
        }
        binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        popup = PopupWindow(
            binding.root,
            binding.root.measuredWidth,
            binding.root.measuredHeight
        )
        popup.isOutsideTouchable = true
        popup.isFocusable = true
    }

    override fun onShortcutSelected() {
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