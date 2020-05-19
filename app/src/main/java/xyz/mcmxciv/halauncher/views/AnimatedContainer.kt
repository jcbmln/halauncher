package xyz.mcmxciv.halauncher.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.widget.LinearLayout
import kotlin.math.hypot

class AnimatedContainer(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var animationStartX = 0
    private var animationStartY = 0
    private val openRadius: Float

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        openRadius = hypot(dm.widthPixels.toFloat(), dm.heightPixels.toFloat())
    }

    fun setAnimationStartPoint(x: Int, y: Int) {
        animationStartX = x
        animationStartY = y
    }

    fun animateOpen(animationListener: (() -> Unit)? = null) {
        val animator = ViewAnimationUtils.createCircularReveal(
            this, animationStartX, animationStartY, 0f, openRadius
        )
        animator.addListener(object : AnimatorListenerAdapter() {
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
            this, animationStartX, animationStartY, openRadius, 0f
        )
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
                animationListener?.invoke()
            }
        })
        animator.start()
    }
}
