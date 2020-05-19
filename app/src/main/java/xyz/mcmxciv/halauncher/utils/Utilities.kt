/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.mcmxciv.halauncher.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.palette.graphics.Palette
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Various utilities shared amongst the Launcher's classes.
 */
object Utilities {
    /**
     * Calculates the height of a given string at a specific text size.
     */
    fun calculateTextHeight(textSizePx: Float): Int {
        val p = Paint()
        p.textSize = textSizePx
        val fm = p.fontMetrics
        return ceil((fm.bottom - fm.top).toDouble()).toInt()
    }

    fun dpiFromPx(size: Int, metrics: DisplayMetrics): Float {
        val densityRatio = metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
        return size / densityRatio
    }

    fun pxFromSp(size: Float, metrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            size, metrics
        ).roundToInt()
    }

    fun pxFromDp(size: Float, metrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, metrics).roundToInt()
    }

    fun isDarkColor(color: Int): Boolean {
        return Color.luminance(color) < 0.5
    }

    fun createColorFromBitmap(bitmap: Bitmap, defaultColor: Int, alpha: Float = 1f): Int {
        val palette = Palette.from(bitmap).generate()
        val dominantColor = palette.getDominantColor(defaultColor)
        val solidColor = Color.valueOf(dominantColor)
        return Color.argb(alpha, solidColor.red(), solidColor.green(), solidColor.blue())
    }
}
