package xyz.mcmxciv.halauncher.ui

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation

class ViewAnimator {
    val showViewAnimationSet = AnimationSet(true)
    val hideViewAnimationSet = AnimationSet(true)
    private var sharedInterpolator: Interpolator? = null
    var duration: Long = 300

    var interpolator: Interpolator?
        get() = sharedInterpolator
        set(value) { sharedInterpolator = value }

    fun createScaleFadeAnimation(pivotX: Float, pivotY: Float) {
        showViewAnimationSet.animations.clear()
        showViewAnimationSet.addAnimation(ScaleAnimation(
            0f, 1f,
            0f, 1f,
            Animation.RELATIVE_TO_SELF, pivotX,
            Animation.RELATIVE_TO_SELF, pivotY
        ))
        showViewAnimationSet.addAnimation(AlphaAnimation(0f, 1f))
        showViewAnimationSet.duration = duration
        showViewAnimationSet.interpolator = sharedInterpolator ?: LinearInterpolator()

        hideViewAnimationSet.animations.clear()
        hideViewAnimationSet.addAnimation(ScaleAnimation(
            1f, 0f,
            1f, 0f,
            Animation.RELATIVE_TO_SELF, pivotX,
            Animation.RELATIVE_TO_SELF, pivotY
        ))
        hideViewAnimationSet.addAnimation(AlphaAnimation(1f, 0f))
        hideViewAnimationSet.duration = duration
        hideViewAnimationSet.interpolator = sharedInterpolator ?: LinearInterpolator()
    }
}
