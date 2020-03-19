package xyz.mcmxciv.halauncher.ui.main.applist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.hypot

class AppListButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : FloatingActionButton(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val animationOpenRadius = hypot(width.toFloat(), height.toFloat())
    private val animationStartX = left + (width / 2)
    private val animationStartY = top + (height / 2)

    fun animateOpen(animationListener: (() -> Unit)? = null) {
        val animator = ViewAnimationUtils.createCircularReveal(
            this, animationStartX, animationStartY, 0f, animationOpenRadius
        )
        animator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animationListener?.invoke()
            }
        })
        visibility = View.VISIBLE
        animator.start()
    }

    fun animateClose(animationListener: (() -> Unit)? = null) {
        val animator = ViewAnimationUtils.createCircularReveal(
            this, animationStartX, animationStartY, animationOpenRadius, 0f
        )
        animator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
                animationListener?.invoke()
            }
        })
        animator.start()
    }
}