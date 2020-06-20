package xyz.mcmxciv.halauncher.utils

import android.util.DisplayMetrics

object Utilities {
    fun dpiFromPx(size: Int, metrics: DisplayMetrics): Float {
        val densityRatio = metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
        return size / densityRatio
    }
}
