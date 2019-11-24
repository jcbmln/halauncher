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

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import android.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap


/**
 * Various utilities shared amongst the Launcher's classes.
 */
object Utilities {
    private const val BITMAP_SCALE = 0.4f

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

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            AppFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE
        )
    }

    fun getDevicePrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            AppFiles.DEVICE_PREFERENCES_KEY, Context.MODE_PRIVATE
        )
    }

    fun drawableToScaledBitmap(drawable: Drawable): Bitmap {
        val image = drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val width = (image.width * BITMAP_SCALE).roundToInt()
        val height = (image.height * BITMAP_SCALE).roundToInt()

        return Bitmap.createScaledBitmap(image, width, height, false)
    }

    fun isDark(drawable: Drawable): Boolean {
        val bitmap = drawableToScaledBitmap(drawable)
        var dark = false

        val darkThreshold = bitmap.width.toFloat() * bitmap.height.toFloat() * 0.45f
        var darkPixels = 0

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixel in pixels) {
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)
            val luminance = 0.299 * r + 0.0 + 0.587 * g + 0.0 + 0.114 * b + 0.0
            if (luminance < 150) {
                darkPixels++
            }
        }

        if (darkPixels >= darkThreshold) {
            dark = true
        }

        return dark
    }
}
