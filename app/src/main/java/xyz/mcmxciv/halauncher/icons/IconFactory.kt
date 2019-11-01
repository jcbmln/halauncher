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
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.graphics.Paint.DITHER_FLAG
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.UserHandle
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.icons.ShadowGenerator.Companion.BLUR_FACTOR
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.round

class IconFactory(private val context: Context,
                  private val fillResIconDpi: Int,
                  private val iconBitmapSize: Int,
                  private val shapeDetection: Boolean
) : AutoCloseable {
    private val oldBounds = Rect()
    private val canvas = Canvas()
    private val packageManager = context.packageManager
    private val colorExtractor = ColorExtractor()
    private var colorExtractorDisabled = false
    private var wrapperIcon: Drawable? = null
    private var wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND

    private var _normalizer: IconNormalizer? = null
    val normalizer: IconNormalizer
        get() = _normalizer ?: IconNormalizer(context, iconBitmapSize, shapeDetection)

    private var _shadowGenerator: ShadowGenerator? = null
    val shadowGenerator: ShadowGenerator
        get() = _shadowGenerator ?: ShadowGenerator(iconBitmapSize)

    init {
        canvas.drawFilter = PaintFlagsDrawFilter(DITHER_FLAG, FILTER_BITMAP_FLAG)
        clear()
    }

    override fun close() {
        clear()
    }

    fun createIconBitmap(icon: Bitmap): BitmapInfo {
        val bitmap = if (iconBitmapSize != icon.width || iconBitmapSize != icon.height)
            createIconBitmap(BitmapDrawable(context.resources, icon), 1f)
            else icon


        return BitmapInfo.fromBitmap(
            bitmap,
            if (colorExtractorDisabled) null else colorExtractor
        )
    }

    private fun createIconBitmap(icon: Drawable, scale: Float): Bitmap {
        return createIconBitmap(icon, scale, iconBitmapSize)
    }

    /**
     * @param icon drawable that should be flattened to a bitmap
     * @param scale the scale to apply before drawing {@param icon} on the canvas
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun createIconBitmap(icon: Drawable, scale: Float, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        oldBounds.set(icon.bounds)

        if (atleastOreo && icon is AdaptiveIconDrawable) {
            val offset = max(
                ceil(BLUR_FACTOR * size).toInt(),
                round(size * (1 - scale) / 2).toInt()
            )

            icon.bounds = Rect(offset, offset, size - offset, size - offset)
            icon.draw(canvas)
        }
        else {
            if (icon is BitmapDrawable) {
                val b = icon.bitmap

                if (bitmap != null && b.density == Bitmap.DENSITY_NONE) {
                    icon.setTargetDensity(context.resources.displayMetrics)
                }
            }

            var width = size
            var height = size

            val intrinsicWidth = icon.intrinsicWidth
            val intrinsicHeight = icon.intrinsicHeight

            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                // Scale the icon proportionally to the icon dimensions
                val ratio = (intrinsicWidth / intrinsicHeight).toFloat()

                if (intrinsicWidth > intrinsicHeight) {
                    height = (width / ratio).toInt()
                }
                else if (intrinsicHeight < intrinsicWidth) {
                    width = (height * ratio).toInt()
                }
            }

            val left = (size - width) / 2
            val top = (size - width) / 2

            icon.bounds = Rect(left, top, left + width, top + height)
            canvas.save()
            canvas.scale(scale, scale, (size / 2).toFloat(), (size / 2).toFloat())
            icon.draw(canvas)
            canvas.restore()
        }

        icon.bounds = oldBounds
        canvas.setBitmap(null)
        return bitmap
    }

    fun createDefaultIcon(user: UserHandle): BitmapInfo {
        return createBadgedIconBitmap(
            getFullResDefaultActivityIcon(fillResIconDpi),
            user, Build.VERSION.SDK_INT
        )
    }

    fun createBadgedIconBitmap(
        icon: Drawable, user: UserHandle,
        iconAppTargetSdk: Int
    ): BitmapInfo {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, false)
    }

    fun createBadgedIconBitmap(
        icon: Drawable, user: UserHandle,
        iconAppTargetSdk: Int, isInstantApp: Boolean
    ): BitmapInfo {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, isInstantApp, null)
    }

    fun createBadgedIconBitmap(
        icon: Drawable, user: UserHandle,
        iconAppTargetSdk: Int, isInstantApp: Boolean, scale: FloatArray?
    ): BitmapInfo {
        val shrinkNonAdaptiveIcons =
            atleastPie || atleastOreo && iconAppTargetSdk >= Build.VERSION_CODES.O
        return createBadgedIconBitmap(icon, user, shrinkNonAdaptiveIcons, isInstantApp, scale)
    }

    /**
     * Creates bitmap using the source drawable and various parameters.
     * The bitmap is visually normalized with other icons and has enough spacing to add shadow.
     *
     * @param icon                      source of the icon
     * @param user                      info can be used for a badge
     * @param shrinkNonAdaptiveIcons    `true` if non adaptive icons should be treated
     * @param isInstantApp              info can be used for a badge
     * @param scale                     returns the scale result from normalization
     * @return a bitmap suitable for disaplaying as an icon at various system UIs.
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun createBadgedIconBitmap(
        icon: Drawable, user: UserHandle?,
        shrinkNonAdaptiveIcons: Boolean, isInstantApp: Boolean, scale: FloatArray?
    ): BitmapInfo {
        val iconScale = scale ?: FloatArray(1)
        var normalizedIcon = normalizeAndWrapToAdaptiveIcon(
            icon, shrinkNonAdaptiveIcons, null, iconScale
        )
        var bitmap = createIconBitmap(icon, iconScale[0])

        if (atleastOreo && icon is AdaptiveIconDrawable) {
            canvas.setBitmap(bitmap)
            shadowGenerator.recreateIcon(Bitmap.createBitmap(bitmap), canvas)
            canvas.setBitmap(null)
        }

        if (isInstantApp) {
            badgeWithDrawable(bitmap, context.getDrawable(R.drawable.ic_instant_app_badge))
        }
        if (user != null) {
            val drawable = FixedSizeBitmapDrawable(bitmap)
            val badged = packageManager.getUserBadgedIcon(drawable, user)
            bitmap = if (badged is BitmapDrawable) {
                badged.bitmap
            } else {
                createIconBitmap(badged, 1f)
            }
        }
        return BitmapInfo.fromBitmap(bitmap, if (colorExtractorDisabled) null else colorExtractor)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun normalizeAndWrapToAdaptiveIcon(
        icon: Drawable,
        shrinkNonAdaptiveIcons: Boolean, outIconBounds: RectF?, outScale: FloatArray
    ): Drawable? {
        var normalizedIcon = icon
        var scale: Float

        if (shrinkNonAdaptiveIcons && atleastOreo) {
            if (wrapperIcon == null) {
                wrapperIcon = context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper)!!
                    .mutate()
            }
            val dr = wrapperIcon as AdaptiveIconDrawable
            dr.setBounds(0, 0, 1, 1)
            val outShape = BooleanArray(1)
            scale = normalizer.getScale(icon, outIconBounds, dr.iconMask, outShape)
            if (icon !is AdaptiveIconDrawable && !outShape[0]) {
                val fsd = dr.foreground as FixedScaleDrawable
                fsd.drawable = icon
                fsd.setScale(scale)
                normalizedIcon = dr
                scale = normalizer.getScale(icon, outIconBounds, null, null)

                (dr.background as ColorDrawable).color = wrapperBackgroundColor
            }
        } else {
            scale = normalizer.getScale(icon, outIconBounds, null, null)
        }

        outScale[0] = scale
        return icon
    }

    /**
     * Adds the {@param badge} on top of {@param target} using the badge dimensions.
     */
    fun badgeWithDrawable(target: Bitmap, badge: Drawable?) {
        canvas.setBitmap(target)
        badgeWithDrawable(canvas, badge!!)
        canvas.setBitmap(null)
    }

    /**
     * Adds the {@param badge} on top of {@param target} using the badge dimensions.
     */
    fun badgeWithDrawable(target: Canvas, badge: Drawable) {
        val badgeSize = context.resources.getDimensionPixelSize(R.dimen.profile_badge_size)
        badge.setBounds(
            iconBitmapSize - badgeSize, iconBitmapSize - badgeSize,
            iconBitmapSize, iconBitmapSize
        )
        badge.draw(target)
    }

    private fun clear() {
        wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND
        colorExtractorDisabled = false
    }

    companion object {
        private const val TAG = "IconFactory"
        private const val DEFAULT_WRAPPER_BACKGROUND = Color.WHITE
        val atleastOreo: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        val atleastPie: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

        fun getFullResDefaultActivityIcon(iconDpi: Int): Drawable {
            return Resources.getSystem().getDrawableForDensity(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    android.R.drawable.sym_def_app_icon
                else
                    android.R.mipmap.sym_def_app_icon,
                iconDpi, null
            )!!
        }
    }

    /**
     * An extension of [BitmapDrawable] which returns the bitmap pixel size as intrinsic size.
     * This allows the badging to be done based on the action bitmap size rather than
     * the scaled bitmap size.
     */
    private class FixedSizeBitmapDrawable(bitmap: Bitmap) : BitmapDrawable(null, bitmap) {
        override fun getIntrinsicHeight(): Int {
            return bitmap.width
        }

        override fun getIntrinsicWidth(): Int {
            return bitmap.width
        }
    }
}