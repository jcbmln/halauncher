package xyz.mcmxciv.halauncher.shortcuts

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.apps.App
import xyz.mcmxciv.halauncher.apps.AppLauncher
import xyz.mcmxciv.halauncher.apps.OnHideAppListener
import xyz.mcmxciv.halauncher.databinding.PopupWindowShortcutBinding
import xyz.mcmxciv.halauncher.utils.HassTheme
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import xyz.mcmxciv.halauncher.utils.Utilities

class ShortcutPopupWindow(
    private val parentView: View,
    private val app: App,
    private val resourceProvider: ResourceProvider,
    private val appLauncher: AppLauncher,
    private val onHideAppListener: OnHideAppListener
) {
    private val window: PopupWindow
    private val horizontalMargin: Int
    private val screenWidth: Int
    private val screenHeight: Int

    private val binding: PopupWindowShortcutBinding =
        PopupWindowShortcutBinding.inflate(LayoutInflater.from(parentView.context))
    private val showPopupAnimationSet = AnimationSet(true)
    private val hidePopupAnimationSet = AnimationSet(true)
    private val sharedInterpolator = LinearInterpolator()

    init {
        val displayMetrics = resourceProvider.displayMetrics
        horizontalMargin = Utilities.pxFromDpi(10f, displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        initializeView()
        initializeTheme()

        window = initializeWindow()
    }

    fun show() {
        val parentViewLocation = IntArray(2)
        parentView.getLocationOnScreen(parentViewLocation)

        val baseXOffset = (parentView.left + (parentView.width / 2)) - (window.width / 2)
        val horizontalPosition = when {
            parentView.left == parentView.marginStart -> HorizontalPosition.LEFT
            baseXOffset + window.width >= screenWidth -> HorizontalPosition.RIGHT
            else -> HorizontalPosition.MIDDLE
        }
        val verticalPosition = when {
            (parentView.bottom + window.height) > screenHeight -> VerticalPosition.TOP
            else -> VerticalPosition.BOTTOM
        }

        val offsetX: Int
        val offsetY: Int
        val pivotX: Float
        val pivotY: Float
        val visibleArrow: ImageView

        when (verticalPosition) {
            VerticalPosition.TOP -> {
                offsetY = parentViewLocation[1] - window.height
                pivotY = PIVOT_Y_TOP
                visibleArrow = binding.bottomArrow
            }
            VerticalPosition.BOTTOM -> {
                offsetY = parentViewLocation[1] + parentView.height
                pivotY = PIVOT_Y_BOTTOM
                visibleArrow = binding.topArrow
            }
        }

        val parentViewMiddle = parentView.width.toFloat() / 2
        when (horizontalPosition) {
            HorizontalPosition.LEFT -> {
                offsetX = parentView.left + horizontalMargin
                pivotX = (offsetX + parentViewMiddle) / screenWidth
                visibleArrow.translationX = parentViewMiddle - visibleArrow.measuredWidth
            }
            HorizontalPosition.RIGHT -> {
                offsetX = screenWidth - window.width - horizontalMargin
                pivotX = (offsetX + window.width - parentViewMiddle) / screenWidth
                visibleArrow.translationX =
                    window.width - parentViewMiddle - (visibleArrow.measuredWidth / 2)
            }
            HorizontalPosition.MIDDLE -> {
                offsetX = baseXOffset
                pivotX = DEFAULT_PIVOT_X
                visibleArrow.translationX =
                    (binding.root.measuredWidth / 2).toFloat() - (visibleArrow.measuredWidth / 2)
            }
        }

        binding.topArrow.isVisible = verticalPosition == VerticalPosition.BOTTOM
        binding.bottomArrow.isVisible = verticalPosition == VerticalPosition.TOP

        initializeAnimations(pivotX, pivotY)
        binding.root.startAnimation(showPopupAnimationSet)
        window.showAtLocation(parentView, Gravity.NO_GRAVITY, offsetX, offsetY)
    }

    private fun dismiss() {
        binding.root.startAnimation(hidePopupAnimationSet)
    }

    private fun initializeWindow(): PopupWindow {
        val w = PopupWindow(binding.root, binding.root.measuredWidth, binding.root.measuredHeight)
        w.isOutsideTouchable = true
        w.isFocusable = true
        w.setTouchInterceptor { v, event ->
            v.performClick()
            if (event.x < 0 || event.x > v.width || event.y < 0 || event.y > v.height) {
                dismiss()
                true
            } else false
        }
        return w
    }

    private fun initializeView() {
        binding.appInfoIcon.setOnClickListener { v ->
            appLauncher.startAppDetailsActivity(app.componentName, v)
            dismiss()
        }
        binding.hideIcon.setOnClickListener {
            onHideAppListener(app.appCacheInfo.activityName)
            dismiss()
        }

        if (!app.shortcuts.isNullOrEmpty()) {
            binding.shortcutList.layoutManager = LinearLayoutManager(binding.shortcutList.context)

            val onShortcutSelectedListener = { dismiss() }
            binding.shortcutList.adapter = ShortcutListAdapter(
                resourceProvider,
                appLauncher,
                onShortcutSelectedListener
            ).also { it.submitList(app.shortcuts) }
        } else {
            binding.shortcutList.isVisible = false
        }

        val wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT
        binding.root.measure(wrapContent, wrapContent)
    }

    private fun initializeTheme() {
        val theme = HassTheme.instance

        binding.topArrow.drawable.setTint(theme.primaryBackgroundColor)
        binding.bottomArrow.drawable.setTint(theme.primaryBackgroundColor)
        binding.systemShortcutsContainer.background.setTint(theme.primaryBackgroundColor)
        binding.shortcutList.background.setTint(theme.primaryBackgroundColor)
        binding.appInfoIcon.icon?.setTint(theme.primaryTextColor)
        binding.appInfoIcon.setTextColor(theme.primaryTextColor)
        binding.hideIcon.icon?.setTint(theme.primaryTextColor)
        binding.hideIcon.setTextColor(theme.primaryTextColor)
        binding.uninstallIcon.icon?.setTint(theme.primaryTextColor)
        binding.uninstallIcon.setTextColor(theme.primaryTextColor)
        binding.disableIcon.icon?.setTint(theme.primaryTextColor)
        binding.disableIcon.setTextColor(theme.primaryTextColor)
    }

    private fun initializeAnimations(pivotX: Float, pivotY: Float) {
        showPopupAnimationSet.animations.clear()
        showPopupAnimationSet.addAnimation(ScaleAnimation(
            0f,
            1f,
            0f,
            1f,
            Animation.RELATIVE_TO_SELF,
            pivotX,
            Animation.RELATIVE_TO_SELF,
            pivotY
        ))
        showPopupAnimationSet.addAnimation(AlphaAnimation(0f, 1f))
        showPopupAnimationSet.duration = 200
        showPopupAnimationSet.interpolator = sharedInterpolator

        hidePopupAnimationSet.animations.clear()
        hidePopupAnimationSet.addAnimation(ScaleAnimation(
            1f,
            0f,
            1f,
            0f,
            Animation.RELATIVE_TO_SELF,
            pivotX,
            Animation.RELATIVE_TO_SELF,
            pivotY
        ))
        hidePopupAnimationSet.addAnimation(AlphaAnimation(1f, 0f))
        hidePopupAnimationSet.duration = 200
        hidePopupAnimationSet.interpolator = sharedInterpolator
        hidePopupAnimationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                window.dismiss()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
    }

    private enum class HorizontalPosition {
        LEFT,
        MIDDLE,
        RIGHT
    }

    private enum class VerticalPosition {
        TOP,
        BOTTOM
    }

    companion object {
        private const val DEFAULT_PIVOT_X = 0.5f
        private const val PIVOT_Y_TOP = 1f
        private const val PIVOT_Y_BOTTOM = 0f
    }
}
