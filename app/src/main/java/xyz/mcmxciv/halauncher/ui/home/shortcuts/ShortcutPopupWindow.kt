package xyz.mcmxciv.halauncher.ui.home.shortcuts

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.databinding.PopupWindowShortcutsBinding
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.ui.ViewAnimator
import xyz.mcmxciv.halauncher.utils.AppLauncher
import xyz.mcmxciv.halauncher.utils.Utilities

class ShortcutPopupWindow(
    private val parentView: View,
    private val appListItem: AppListItem,
    private val appLauncher: AppLauncher,
    private val listener: ShortcutActionListener
) : ShortcutListAdapter.ShorcutSelectedListener {
    private val binding: PopupWindowShortcutsBinding
    private val window: PopupWindow
    private val leftRightMargin: Int
    private val screenWidth: Int
    private val screenHeight: Int
    private val viewAnimator = ViewAnimator()
    private val theme = HassTheme.instance

    init {
        val resources = parentView.context.resources
        val dm = resources.displayMetrics
        leftRightMargin = Utilities.pxFromDp(10f, dm)
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        binding = PopupWindowShortcutsBinding.inflate(LayoutInflater.from(parentView.context))

        binding.appInfoText.setOnClickListener { view ->
            appLauncher.startAppDetailsActivity(appListItem.componentName, view)
            dismiss()
        }

        binding.hideText.setOnClickListener {
            listener.onHideActivity(appListItem.activityName)
            dismiss()
        }

        if (appListItem.isSystemApp) {
            binding.uninstallText.isEnabled = false
        } else {
            binding.uninstallText.setOnClickListener {
                dismiss()
                appLauncher.uninstall(appListItem.componentName)
            }
        }

        val shortcutItems = appListItem.shortcutItems
        if (shortcutItems != null && shortcutItems.isNotEmpty()) {
            binding.shortcutList.layoutManager = LinearLayoutManager(binding.shortcutList.context)
            binding.shortcutList.adapter =
                ShortcutListAdapter(
                    shortcutItems,
                    this
                )
        } else {
            binding.shortcutList.isVisible = false
        }

        binding.root.measure(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        binding.topArrow.drawable.setTint(theme.primaryBackgroundColor)
        binding.bottomArrow.drawable.setTint(theme.primaryBackgroundColor)
        binding.systemShortcuts.background.setTint(theme.primaryBackgroundColor)
        binding.shortcutList.background.setTint(theme.primaryBackgroundColor)
        binding.appInfoText.apply {
            topIcon?.setTint(theme.accentColor)
            setTextColor(theme.primaryTextColor)
        }
        binding.hideText.apply {
            topIcon?.setTint(theme.accentColor)
            setTextColor(theme.primaryTextColor)
        }
        binding.uninstallText.apply {
            if (!appListItem.isSystemApp) {
                topIcon?.setTint(theme.accentColor)
                setTextColor(theme.primaryTextColor)
            } else {
                topIcon?.setTint(theme.disabledTextColor)
                setTextColor(theme.disabledTextColor)
            }
        }

        window = PopupWindow(
            binding.root,
            binding.root.measuredWidth,
            binding.root.measuredHeight
        )

        window.isOutsideTouchable = true
        window.isFocusable = true
        window.setTouchInterceptor { view, event ->
            if (
                event.x < 0 || event.x > view.width ||
                event.y < 0 || event.y > view.height
            ) {
                dismiss()
                true
            } else false
        }

        viewAnimator.duration = 200
    }

    fun show() {
        val baseXOffset = (parentView.left + (parentView.width / 2)) - (window.width / 2)
        val horizontalLocation = when {
            parentView.left == parentView.marginStart -> HorizontalLocation.LEFT
            baseXOffset + window.width >= screenWidth -> HorizontalLocation.RIGHT
            else -> HorizontalLocation.MIDDLE
        }
        val verticalLocation = when {
            (parentView.bottom + window.height) > screenHeight -> VerticalLocation.TOP
            else -> VerticalLocation.BOTTOM
        }
        val xOffset = when (horizontalLocation) {
            HorizontalLocation.LEFT -> parentView.left + leftRightMargin
            HorizontalLocation.RIGHT -> screenWidth - window.width - leftRightMargin
            HorizontalLocation.MIDDLE -> baseXOffset
        }

        val parentViewLocation = IntArray(2)
        parentView.getLocationOnScreen(parentViewLocation)

        val yOffset = when (verticalLocation) {
            VerticalLocation.TOP -> parentViewLocation[1] - window.height
            VerticalLocation.BOTTOM -> parentViewLocation[1] + parentView.height
        }
        val pivotX = when (horizontalLocation) {
            HorizontalLocation.LEFT ->
                (xOffset + (parentView.width / 2)).toFloat() / screenWidth
            HorizontalLocation.RIGHT ->
                (xOffset + window.width - (parentView.width / 2)).toFloat() / screenWidth
            HorizontalLocation.MIDDLE -> 0.5f
        }
        val pivotY = when (verticalLocation) {
            VerticalLocation.TOP -> 1f
            VerticalLocation.BOTTOM -> 0f
        }

        binding.topArrow.isVisible = verticalLocation == VerticalLocation.BOTTOM
        binding.bottomArrow.isVisible = verticalLocation == VerticalLocation.TOP

        val visibleArrow = when (verticalLocation) {
            VerticalLocation.TOP -> binding.bottomArrow
            VerticalLocation.BOTTOM -> binding.topArrow
        }

        visibleArrow.translationX = when (horizontalLocation) {
            HorizontalLocation.LEFT ->
                (parentView.width / 2) - visibleArrow.measuredWidth
            HorizontalLocation.RIGHT ->
                window.width - (parentView.width / 2) - (visibleArrow.measuredWidth / 2)
            HorizontalLocation.MIDDLE ->
                (binding.root.measuredWidth / 2) - (visibleArrow.measuredWidth / 2)
        }.toFloat()

        viewAnimator.createScaleFadeAnimation(pivotX, pivotY)
        binding.root.startAnimation(viewAnimator.showViewAnimationSet)
        window.showAtLocation(parentView, Gravity.NO_GRAVITY, xOffset, yOffset)
    }

    private fun dismiss() {
        viewAnimator
            .hideViewAnimationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                window.dismiss()
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        binding.root.startAnimation(viewAnimator.hideViewAnimationSet)
    }

    override fun onShortcutSelected() {
        dismiss()
    }

    private enum class HorizontalLocation {
        LEFT,
        MIDDLE,
        RIGHT
    }

    private enum class VerticalLocation {
        TOP,
        BOTTOM
    }

    interface ShortcutActionListener {
        fun onHideActivity(activityName: String)
    }
}
