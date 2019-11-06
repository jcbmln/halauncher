/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.graphics.Paint.DITHER_FLAG
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.drawable.*
import xyz.mcmxciv.halauncher.R

class IconFactory(private val context: Context,
                  private val iconBitmapSize: Int
) : AutoCloseable {
    private val canvas = Canvas()
    private var colorExtractorDisabled = false
    private var wrapperIcon: Drawable? = null
    private var wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND

    private var _normalizer: IconNormalizer? = null
    private val normalizer: IconNormalizer
        get() = _normalizer ?: IconNormalizer(context, iconBitmapSize)

    init {
        canvas.drawFilter = PaintFlagsDrawFilter(DITHER_FLAG, FILTER_BITMAP_FLAG)
        clear()
    }

    override fun close() {
        clear()
    }

    private fun createIconBitmap(icon: Drawable): Bitmap {
        return createIconBitmap(icon, iconBitmapSize)
    }

    /**
     * @param icon drawable that should be flattened to a bitmap
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun createIconBitmap(icon: Drawable, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        icon.draw(canvas)
        canvas.drawBitmap(getMaskBitmap(), 0f, 0f, maskPaint)

        return bitmap
//        canvas.setBitmap(bitmap)
//        oldBounds.set(icon.bounds)
//
//        if (atleastOreo && icon is AdaptiveIconDrawable) {
//            val offset = max(
//                ceil(BLUR_FACTOR * size).toInt(),
//                round(size * (1 - scale) / 2).toInt()
//            )
//
//            icon.bounds = Rect(offset, offset, size - offset, size - offset)
//            icon.draw(canvas)
//        }
//        else {
//            if (icon is BitmapDrawable) {
//                val b = icon.bitmap
//
//                if (bitmap != null && b.density == Bitmap.DENSITY_NONE) {
//                    icon.setTargetDensity(context.resources.displayMetrics)
//                }
//            }
//
//            var width = size
//            var height = size
//
//            val intrinsicWidth = icon.intrinsicWidth
//            val intrinsicHeight = icon.intrinsicHeight
//
//            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
//                // Scale the icon proportionally to the icon dimensions
//                val ratio = (intrinsicWidth / intrinsicHeight).toFloat()
//
//                if (intrinsicWidth > intrinsicHeight) {
//                    height = (width / ratio).toInt()
//                }
//                else if (intrinsicHeight < intrinsicWidth) {
//                    width = (height * ratio).toInt()
//                }
//            }
//
//            val left = (size - width) / 2
//            val top = (size - width) / 2
//
//            icon.bounds = Rect(left, top, left + width, top + height)
//            canvas.save()
//            canvas.scale(scale, scale, (size / 2).toFloat(), (size / 2).toFloat())
//            icon.draw(canvas)
//            canvas.restore()
//        }
//
//        icon.bounds = oldBounds
//        canvas.setBitmap(null)
    }

    private fun getMaskBitmap(): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val bitmap = Bitmap.createBitmap(iconBitmapSize, iconBitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawPath(IconShape.shapePath, paint)

        return bitmap
    }

    fun createIcon(icon: Drawable): Bitmap {
        val shrinkNonAdaptiveIcons =
            atleastPie || atleastOreo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        return createIcon(icon, shrinkNonAdaptiveIcons)
    }

    /**
     * Creates bitmap using the source drawable and various parameters.
     * The bitmap is visually normalized with other icons and has enough spacing to add shadow.
     *
     * @param icon                      source of the icon
     * @param shrinkNonAdaptiveIcons    `true` if non adaptive icons should be treated
     * @return a bitmap suitable for disaplaying as an icon at various system UIs.
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun createIcon(icon: Drawable, shrinkNonAdaptiveIcons: Boolean): Bitmap {
        val normalizedIcon = normalizeAndWrapToAdaptiveIcon(icon, shrinkNonAdaptiveIcons)
        val bitmap = createIconBitmap(normalizedIcon)

        if (atleastOreo && icon is AdaptiveIconDrawable) {
            canvas.setBitmap(bitmap)
            //shadowGenerator.recreateIcon(Bitmap.createBitmap(bitmap), canvas)
            canvas.setBitmap(null)
        }

        return bitmap
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun normalizeAndWrapToAdaptiveIcon(
        icon: Drawable, shrinkNonAdaptiveIcons: Boolean
    ): Drawable {
        var normalizedIcon = icon

        if (shrinkNonAdaptiveIcons && atleastOreo && icon !is AdaptiveIconDrawable) {
            if (wrapperIcon == null) {
                wrapperIcon = context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper)!!
                    .mutate()
            }

            val dr = wrapperIcon as AdaptiveIconDrawable
            dr.setBounds(0, 0, 1, 1)

            val scale = normalizer.getScale(icon)
            val fsd = dr.foreground as FixedScaleDrawable
            fsd.drawable = icon
            fsd.setScale(scale)
            normalizedIcon = dr

            (dr.background as ColorDrawable).color = wrapperBackgroundColor
        }

        return createUnmaskedAdaptiveIcon(normalizedIcon)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createUnmaskedAdaptiveIcon(icon: Drawable): Drawable {
        return if (atleastOreo && icon is AdaptiveIconDrawable) {
            val bg = icon.background
            val fg = icon.foreground

            bg.setBounds(0, 0, iconBitmapSize, iconBitmapSize)
            fg.setBounds(0, 0, iconBitmapSize, iconBitmapSize)

            LayerDrawable(arrayOf(bg, fg))
        } else icon
    }

    private fun clear() {
        wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND
        colorExtractorDisabled = false
    }

    companion object {
        private const val DEFAULT_WRAPPER_BACKGROUND = Color.WHITE
        val atleastOreo: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        val atleastPie: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
}