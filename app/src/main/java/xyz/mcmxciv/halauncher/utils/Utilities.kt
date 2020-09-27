package xyz.mcmxciv.halauncher.utils

import android.graphics.Rect
import android.graphics.Region
import android.graphics.RegionIterator
import android.util.DisplayMetrics
import android.util.TypedValue
import kotlin.math.roundToInt

object Utilities {
    fun dpiFromPx(size: Int, metrics: DisplayMetrics): Float {
        val densityRatio = metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
        return size / densityRatio
    }

    fun pxFromDpi(size: Float, metrics: DisplayMetrics): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, metrics).roundToInt()

    fun getArea(region: Region): Float {
        val iterator = RegionIterator(region)
        var area = 0f
        val tempRect = Rect()

        while (iterator.next(tempRect)) {
            area += tempRect.width() * tempRect.height()
        }

        return area
    }
}
