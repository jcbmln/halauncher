package xyz.mcmxciv.halauncher.ui.main

import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.PopupWindowShortcutsBinding
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import xyz.mcmxciv.halauncher.utils.AppLauncher
import xyz.mcmxciv.halauncher.utils.Utilities


class ShortcutPopupWindow(
    private val parentView: View,
    private val context: Context,
    private val appListItem: AppListItem,
    private val appLauncher: AppLauncher
) : ShortcutListAdapter.ShorcutSelectedListener {
    private val binding: PopupWindowShortcutsBinding
    private val window: PopupWindow
    private val leftRightMargin: Int
    private val screenWidth: Int
    private val screenHeight: Int

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        leftRightMargin = Utilities.pxFromDp(10f, dm)
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PopupWindowShortcutsBinding.inflate(inflater)

        binding.appInfoText.setOnClickListener { view ->
            appLauncher.startAppDetailsActivity(appListItem.componentName, view)
            dismiss()
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
            binding.shortcutList.adapter =
                ShortcutListAdapter(
                    context,
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

        window = PopupWindow(
            binding.root,
            binding.root.measuredWidth,
            binding.root.measuredHeight
        )

        window.isOutsideTouchable = true
        window.isFocusable = true
        window.setTouchInterceptor { view, event ->
            if (
                event.x < 0
                || event.x > view.width
                || event.y < 0
                || event.y > view.height
            ) {
                dismiss()
                true
            } else false
        }
    }

    fun show() {
        val xPos = when(horizontalLocation) {
            HorizontalLocation.LEFT -> parentView.left + leftRightMargin
            HorizontalLocation.RIGHT -> screenWidth - window.width - leftRightMargin
            HorizontalLocation.MIDDLE -> baseXPos
        }
        val yPos = when(verticalPosition) {
            VerticalPosition.TOP -> parentViewLocation[1] - window.height
            VerticalPosition.BOTTOM -> parentViewLocation[1] + parentView.height
        }
        val pivotX = getPivotX(xPos)
        val pivotY = when(verticalPosition) {
            VerticalPosition.TOP -> 1f
            VerticalPosition.BOTTOM -> 0f
        }

        val scaleAnimation = ScaleAnimation(
            0f, 1f,
            0f, 1f,
            Animation.RELATIVE_TO_SELF, pivotX,
            Animation.RELATIVE_TO_SELF, pivotY
        )
        val alphaAnimation = AlphaAnimation(0f, 1f)

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(alphaAnimation)
        animationSet.interpolator = AccelerateDecelerateInterpolator()
        animationSet.duration = 150

        visibleArrow.translationX = when(horizontalLocation) {
            HorizontalLocation.LEFT ->
                (parentView.width / 2) - visibleArrow.measuredWidth
            HorizontalLocation.RIGHT ->
                window.width - (parentView.width / 2)
            HorizontalLocation.MIDDLE ->
                (binding.root.measuredWidth / 2) - (visibleArrow.measuredWidth / 2)
        }.toFloat()
        binding.root.startAnimation(animationSet)
        window.showAtLocation(parentView, Gravity.NO_GRAVITY, xPos, yPos)
    }

    private fun dismiss() {
        window.dismiss()
    }

    override fun onShortcutSelected() {
        dismiss()
    }

    private val baseXPos: Int
        get() = (parentView.left + (parentView.width / 2)) - (window.width / 2)

    private val horizontalLocation: HorizontalLocation
        get() = when {
            parentView.left == parentView.marginStart -> HorizontalLocation.LEFT
            baseXPos + window.width >= screenWidth -> HorizontalLocation.RIGHT
            else -> HorizontalLocation.MIDDLE
        }

    private val verticalPosition: VerticalPosition
        get() = when {
            (parentView.bottom + window.height) > screenHeight -> VerticalPosition.TOP
            else -> VerticalPosition.BOTTOM
        }

    private val visibleArrow: ImageView
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

    private fun getPivotX(xPos: Int): Float =
        when (horizontalLocation) {
            HorizontalLocation.LEFT ->
                (xPos + (parentView.width / 2)).toFloat() / screenWidth
            HorizontalLocation.RIGHT ->
                (xPos + window.width - (parentView.width / 2)).toFloat() / screenWidth
            HorizontalLocation.MIDDLE -> 0.5f
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