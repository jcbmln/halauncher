package xyz.mcmxciv.halauncher.anim


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Outline
import android.graphics.Rect
import android.view.View
import android.view.ViewOutlineProvider

/**
 * A [ViewOutlineProvider] that has helper functions to create reveal animations.
 * This class should be extended so that subclasses can define the reveal shape as the
 * animation progresses from 0 to 1.
 */
abstract class RevealOutlineAnimation : ViewOutlineProvider() {
    protected var outline: Rect = Rect()
    protected var radius: Float = 0.toFloat()

    /** Returns whether elevation should be removed for the duration of the reveal animation.  */
    internal abstract fun shouldRemoveElevationDuringAnimation(): Boolean

    /** Sets the progress, from 0 to 1, of the reveal animation.  */
    internal abstract fun setProgress(progress: Float)

    fun createRevealAnimator(revealView: View, isReversed: Boolean): ValueAnimator {
        val va = if (isReversed) ValueAnimator.ofFloat(1f, 0f) else ValueAnimator.ofFloat(0f, 1f)
        val elevation = revealView.elevation

        va.addListener(object : AnimatorListenerAdapter() {
            private var mIsClippedToOutline: Boolean = false
            private var mOldOutlineProvider: ViewOutlineProvider? = null

            override fun onAnimationStart(animation: Animator) {
                mIsClippedToOutline = revealView.clipToOutline
                mOldOutlineProvider = revealView.outlineProvider

                revealView.outlineProvider = this@RevealOutlineAnimation
                revealView.clipToOutline = true
                if (shouldRemoveElevationDuringAnimation()) {
                    revealView.translationZ = -elevation
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                revealView.outlineProvider = mOldOutlineProvider
                revealView.clipToOutline = mIsClippedToOutline
                if (shouldRemoveElevationDuringAnimation()) {
                    revealView.translationZ = 0f
                }
            }

        })

        va.addUpdateListener { v ->
            val progress = v.animatedValue as Float
            setProgress(progress)
            revealView.invalidateOutline()
        }
        return va
    }

    override fun getOutline(v: View, outline: Outline) {
        outline.setRoundRect(this.outline, radius)
    }

    fun getOutline(out: Rect) {
        out.set(outline)
    }
}
