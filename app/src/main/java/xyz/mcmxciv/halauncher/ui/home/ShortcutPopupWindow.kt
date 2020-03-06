package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.PopupWindowShortcutsBinding
import xyz.mcmxciv.halauncher.models.apps.ShortcutItem
import xyz.mcmxciv.halauncher.utils.Utilities

class ShortcutPopupWindow(
    context: Context,
    shortcutItems: List<ShortcutItem>?
) : ShortcutListAdapter.ShorcutSelectedListener {
    private val binding: PopupWindowShortcutsBinding
    private val popup: PopupWindow
    private val dm = DisplayMetrics()

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PopupWindowShortcutsBinding.inflate(inflater)

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
    }

    fun show(view: View) {
        val baseXPos = view.left - (view.width / 2)
        val xPos = when {
            view.left == 0 -> view.left + Utilities.pxFromDp(10f, dm)
            baseXPos + popup.width >= dm.widthPixels ->
                dm.widthPixels - popup.width - Utilities.pxFromDp(10f, dm)
            else -> baseXPos
        }
        val yPos = when {
            (view.bottom + popup.height) > dm.heightPixels ->
                view.top + (view.height / 2) - popup.height
            else -> view.bottom + (view.height / 2)
        }

        popup.showAtLocation(view, Gravity.NO_GRAVITY, xPos, yPos)
    }

    override fun onShortcutSelected() {
        popup.dismiss()
    }
}