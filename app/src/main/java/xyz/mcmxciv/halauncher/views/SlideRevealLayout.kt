package xyz.mcmxciv.halauncher.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SlideRevealLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val autoOpenSpeedLimit = 800.0
    private var currentDragState = 0
    private lateinit var dragHelper: ViewDragHelper
    private var currentHorizontalPosition = 0
    private var touchSlop = 0
    private var initialX = 0
    private var shouldDrag = false
    private var closedPosition: Int = 0
    private var openPosition: Int = 0
    private var topBound: Int = 0

    private lateinit var _slidableView: View
    private lateinit var _revealableView: View

    var slidableView: View
        get() = _slidableView
        set(value) {
            _slidableView = value
        }

    var revealableView: View
        get() = _revealableView
        set(value) {
            _revealableView = value
        }

//    val isMoving: Boolean
//        get() = currentDragState == ViewDragHelper.STATE_DRAGGING ||
//                currentDragState == ViewDragHelper.STATE_SETTLING

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        viewTreeObserver.addOnGlobalLayoutListener {
            closedPosition = _slidableView.left
            // doubling the closedPosition is needed to cancel out the offset
            // of the drag view's initial left position
            val offset = _revealableView.width +
                    _revealableView.marginLeft +
                    _revealableView.marginRight
            openPosition = (closedPosition) - offset
            topBound = _slidableView.top
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        dragHelper = ViewDragHelper.create(this, 1.0F, DragHelperCallback())
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // only attempt to intercept if the dragView is the target
        if (!(isDragTarget(event) && isEnabled)) {
            return false
        }

        when (event.actionMasked) {
            // get the initial x coordinate and reset shouldDrag
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x.toInt()
                shouldDrag = false
            }
            // if the difference between the initial x coordinate and the new x coordinate
            // is greater than the touch slop, the view should be dragged
            MotionEvent.ACTION_MOVE -> {
                val xDistance = calculateXDistance(event)
                shouldDrag = xDistance > touchSlop
            }
        }

        return shouldDrag || dragHelper.shouldInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    // if the event's x coordinate is within the bounds of the dragView, then it is the target
    private fun isDragTarget(event: MotionEvent): Boolean {
        val viewLocation = IntArray(2)
        _slidableView.getLocationOnScreen(viewLocation)

        val measuredWidth = _slidableView.measuredWidth
        val upperLimit = viewLocation[0] + measuredWidth
        val lowerLimit = viewLocation[0]
        val x = event.rawX.toInt()

        return (x in (lowerLimit + 1) until upperLimit)
    }

    private fun calculateXDistance(event: MotionEvent): Int {
        return abs(initialX - event.x).toInt()
    }

    inner class DragHelperCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == _slidableView
        }

        // the view should only be dragged as long as the left side is between
        // the openPosition and closedPosition
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return min(max(left, openPosition), closedPosition)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return topBound
        }

        override fun onViewDragStateChanged(state: Int) {
            currentDragState = state
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            currentHorizontalPosition = left
        }

        // the left bound may be negative; get the absolute value
        // so the processTouchEvent works
        override fun getViewHorizontalDragRange(child: View): Int {
            return abs(openPosition)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            // it's closed, so do nothing
            if (currentHorizontalPosition == closedPosition) {
                return
            }

            // it's open so do nothing
            if (currentHorizontalPosition == openPosition) {
                return
            }

            var shouldOpen = false

            // choose to open or close depending on speed of drag and position
            when {
                xvel > autoOpenSpeedLimit -> shouldOpen = true
                xvel < -autoOpenSpeedLimit -> shouldOpen = false
                currentHorizontalPosition > openPosition / 2 -> shouldOpen = true
                currentHorizontalPosition < openPosition / 2 -> shouldOpen = false
            }

            val x = if (shouldOpen) openPosition else closedPosition

            if (dragHelper.settleCapturedViewAt(x, topBound)) {
                ViewCompat.postInvalidateOnAnimation(this@SlideRevealLayout)
            }
        }
    }
}
