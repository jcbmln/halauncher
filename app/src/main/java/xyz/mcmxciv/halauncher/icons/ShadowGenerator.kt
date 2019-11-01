/*
 * Copyright (C) 2016 The Android Open Source Project
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

package xyz.mcmxciv.halauncher.icons

import android.graphics.*
import xyz.mcmxciv.halauncher.utils.GraphicsUtils.setColorAlphaBound
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Utility class to add shadows to bitmaps.
 */
class ShadowGenerator(private val iconSize: Int) {
    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val defaultBlurMaskFilter = BlurMaskFilter(iconSize * BLUR_FACTOR,
        BlurMaskFilter.Blur.NORMAL)

    @Synchronized
    fun recreateIcon(icon: Bitmap, out: Canvas) {
        recreateIcon(icon, defaultBlurMaskFilter, AMBIENT_SHADOW_ALPHA, KEY_SHADOW_ALPHA, out)
    }

    @Synchronized
    fun recreateIcon(
        icon: Bitmap,
        blurMaskFilter: BlurMaskFilter,
        ambientAlpha: Int,
        keyAlpha: Int,
        out: Canvas)
    {
        val offset = IntArray(2)
        blurPaint.maskFilter = blurMaskFilter
        val shadow = icon.extractAlpha(blurPaint, offset)

        // Draw ambient shadow
        drawPaint.alpha = ambientAlpha
        out.drawBitmap(shadow, offset[0].toFloat(), offset[1].toFloat(), drawPaint)

        // Draw key shadow
        drawPaint.alpha = keyAlpha
        out.drawBitmap(
            shadow,
            offset[0].toFloat(),
            offset[1].toFloat() + KEY_SHADOW_DISTANCE * iconSize,
            drawPaint
        )
    }

    companion object {
        const val BLUR_FACTOR = 0.5f / 48

        // Percent of actual icon size
        const val KEY_SHADOW_DISTANCE = 1f / 48
        private const val KEY_SHADOW_ALPHA = 61

        // Percent of actual icon size
        private const val HALF_DISTANCE = 0.5f
        private const val AMBIENT_SHADOW_ALPHA = 30

        /**
         * Returns the minimum amount by which an icon with {@param bounds} should be scaled
         * so that the shadows do not get clipped.
         */
        fun getScaleForBounds(bounds: RectF): Float {
            // For top, left & right, we need same space.
            val minSide = min(min(bounds.left, bounds.right), bounds.top)

            var scale = if (minSide < BLUR_FACTOR)
                ((HALF_DISTANCE - BLUR_FACTOR) / (HALF_DISTANCE - minSide))
                else 1f

            val bottomSpace = BLUR_FACTOR + KEY_SHADOW_DISTANCE
            if (bounds.bottom < bottomSpace) {
                scale = min(scale, (HALF_DISTANCE - bottomSpace) /  (HALF_DISTANCE - bounds.bottom))
            }

            return scale
        }
    }

    class Builder(private val color: Int) {
        val bounds = RectF()
        var ambientShadowAlpha = AMBIENT_SHADOW_ALPHA
        var shadowBlur: Float = 0.toFloat()
        var keyShadowDistance: Float = 0.toFloat()
        var keyShadowAlpha = KEY_SHADOW_ALPHA
        var radius: Float = 0.toFloat()

        fun setupBlurForSize(height: Int): Builder {
            shadowBlur = height * 1f / 24
            keyShadowDistance = height * 1f / 16
            return this
        }

        @JvmOverloads
        fun createPill(width: Int, height: Int, r: Float = height / 2f): Bitmap {
            radius = r

            val centerX = (width / 2f + shadowBlur).roundToInt()
            val centerY = (radius + shadowBlur + keyShadowDistance).roundToInt()
            val center = max(centerX, centerY)
            bounds.set(0f, 0f, width.toFloat(), height.toFloat())
            bounds.offsetTo(center - width / 2f, center - height / 2f)

            val size = center * 2
            val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            drawShadow(Canvas(result))
            return result
        }

        fun drawShadow(c: Canvas) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            p.color = color

            // Key shadow
            p.setShadowLayer(
                shadowBlur, 0f, keyShadowDistance,
                setColorAlphaBound(Color.BLACK, keyShadowAlpha)
            )
            c.drawRoundRect(bounds, radius, radius, p)

            // Ambient shadow
            p.setShadowLayer(
                shadowBlur, 0f, 0f,
                setColorAlphaBound(Color.BLACK, ambientShadowAlpha)
            )
            c.drawRoundRect(bounds, radius, radius, p)

            if (Color.alpha(color) < 255) {
                // Clear any content inside the pill-rect for translucent fill.
                p.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                p.clearShadowLayer()
                p.color = Color.BLACK
                c.drawRoundRect(bounds, radius, radius, p)

                p.xfermode = null
                p.color = color
                c.drawRoundRect(bounds, radius, radius, p)
            }
        }
    }
}