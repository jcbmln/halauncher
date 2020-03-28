package xyz.mcmxciv.halauncher.ui.main.shortcuts

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.R
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
    theme: HassTheme?
) : ShortcutListAdapter.ShorcutSelectedListener {
    private val binding: PopupWindowShortcutsBinding
    private val window: PopupWindow
    private val leftRightMargin: Int
    private val screenWidth: Int
    private val screenHeight: Int
    private val viewAnimator = ViewAnimator()

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

        if (appListItem.isSystemApp) {
            binding.uninstallText.isEnabled = false
            binding.uninstallText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                parentView.context.getDrawable(R.drawable.ic_remove_disabled),
                null,
                null
            )
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

        theme?.let {
            binding.topArrow.drawable.setTint(it.primaryColor)
            binding.bottomArrow.drawable.setTint(it.primaryColor)
            binding.systemShortcuts.background.setTint(it.primaryColor)
            binding.shortcutList.background.setTint(it.primaryColor)
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
                event.x < 0 || event.x > view.width
                || event.y < 0 || event.y > view.height
            ) {
                dismiss()
                true
            } else false
        }

        viewAnimator.duration = 200
    }

    fun show() {
        visibleArrow.translationX = when(horizontalLocation) {
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

    private val baseXOffset: Int
        get() = (parentView.left + (parentView.width / 2)) - (window.width / 2)

    private val xOffset: Int
        get() = when(horizontalLocation) {
            HorizontalLocation.LEFT -> parentView.left + leftRightMargin
            HorizontalLocation.RIGHT -> screenWidth - window.width - leftRightMargin
            HorizontalLocation.MIDDLE -> baseXOffset
        }

    private val yOffset: Int
        get() = when(verticalPosition) {
            VerticalPosition.TOP -> parentViewLocation[1] - window.height
            VerticalPosition.BOTTOM -> parentViewLocation[1] + parentView.height
        }

    private val pivotX: Float
        get() = when (horizontalLocation) {
            HorizontalLocation.LEFT ->
                (xOffset + (parentView.width / 2)).toFloat() / screenWidth
            HorizontalLocation.RIGHT ->
                (xOffset + window.width - (parentView.width / 2)).toFloat() / screenWidth
            HorizontalLocation.MIDDLE -> 0.5f
        }

    private val pivotY: Float
        get() = when(verticalPosition) {
            VerticalPosition.TOP -> 1f
            VerticalPosition.BOTTOM -> 0f
        }

    private val horizontalLocation: HorizontalLocation
        get() = when {
            parentView.left == parentView.marginStart -> HorizontalLocation.LEFT
            baseXOffset + window.width >= screenWidth -> HorizontalLocation.RIGHT
            else -> HorizontalLocation.MIDDLE
        }

    private val verticalPosition: VerticalPosition
        get() = when {
            (parentView.bottom + window.height) > screenHeight -> VerticalPosition.TOP
            else -> VerticalPosition.BOTTOM
        }

    private val visibleArrow: View
        get() {
            binding.topArrow.isVisible = verticalPosition == VerticalPosition.BOTTOM
            binding.bottomArrow.isVisible = verticalPosition == VerticalPosition.TOP

            return when(verticalPosition) {
                VerticalPosition.TOP -> binding.bottomArrow
                VerticalPosition.BOTTOM -> binding.topArrow
            }
        }

    private val parentViewLocation: IntArray
        get() {
            val position = IntArray(2)
            parentView.getLocationOnScreen(position)
            return position
        }


    private enum class HorizontalLocation {
        LEFT,
        MIDDLE,
        RIGHT
    }

    private enum class VerticalPosition {
        TOP,
        BOTTOM
    }
}