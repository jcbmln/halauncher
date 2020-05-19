package xyz.mcmxciv.halauncher.extensions

import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Toast

// https://stackoverflow.com/a/21026866/4496033
fun Toast.setPosition(view: View, window: Window, offsetX: Int, offsetY: Int) {
    val rect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)

    val viewLocation = IntArray(2)
    view.getLocationInWindow(viewLocation)

    val viewLeft = viewLocation[0] - rect.left
    val viewTop = viewLocation[1] - rect.top

    val dm = DisplayMetrics()
    window.windowManager.defaultDisplay.getMetrics(dm)

    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(dm.widthPixels,
        View.MeasureSpec.UNSPECIFIED)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(dm.heightPixels,
        View.MeasureSpec.UNSPECIFIED)
    this.view.measure(widthMeasureSpec, heightMeasureSpec)
    val toastWidth = this.view.measuredWidth

    val toastX = viewLeft + (view.width - toastWidth) / 2 + offsetX
    val toastY = viewTop + view.height + offsetY

    this.setGravity(Gravity.TOP or Gravity.START, toastX, toastY)
}
