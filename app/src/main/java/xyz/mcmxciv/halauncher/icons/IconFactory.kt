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

import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.res.Resources.NotFoundException
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import timber.log.Timber
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.round

@Singleton
class IconFactory @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val packageManager: PackageManager,
    private val invariantDeviceProfile: InvariantDeviceProfile,
    private val iconNormalizer: IconNormalizer,
    private val shadowGenerator: ShadowGenerator,
    private val launcherApps: LauncherApps
) {
    private var wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND
    private val canvas = Canvas()
    private val oldBounds = Rect()

    init {
        canvas.drawFilter = PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG)
    }

    fun getIcon(launcherActivityInfo: LauncherActivityInfo): Bitmap {
        val drawable = getDrawable(launcherActivityInfo)
            ?: launcherActivityInfo.getIcon(invariantDeviceProfile.fillResIconDpi)
        return createIconBitmap(drawable)
    }

    fun getShortcutIcon(shortcutInfo: ShortcutInfo): Bitmap? {
        return launcherApps.getShortcutIconDrawable(
            shortcutInfo,
            invariantDeviceProfile.shortcutIconDpi
        )?.toBitmap(
            invariantDeviceProfile.shortcutBitmapSize,
            invariantDeviceProfile.shortcutBitmapSize
        )
    }


    private fun getDrawable(launcherActivityInfo: LauncherActivityInfo): Drawable? {
        val iconRes = launcherActivityInfo.applicationInfo.icon
        val density = invariantDeviceProfile.fillResIconDpi

        return if (density != 0 && iconRes != 0) {
            try {
                packageManager
                    .getResourcesForApplication(launcherActivityInfo.applicationInfo)
                    .getDrawableForDensity(iconRes, density, null)
            } catch (ex: PackageManager.NameNotFoundException) {
                Timber.e(ex)
                null
            } catch (ex: NotFoundException) {
                Timber.e(ex)
                null
            }
        } else null
    }

    private fun createIconBitmap(icon: Drawable): Bitmap {
        val scale = FloatArray(1)
        val normalizedIcon = normalizeAndWrapToAdaptiveIcon(icon, scale)
        val bitmap = createIconBitmap(normalizedIcon, scale[0])

        if (ATLEAST_OREO && icon is AdaptiveIconDrawable) {
            canvas.setBitmap(bitmap)
            shadowGenerator.recreateIcon(Bitmap.createBitmap(bitmap), canvas)
            canvas.setBitmap(null)
        }

        return bitmap
    }

    private fun createIconBitmap(icon: Drawable, scale: Float): Bitmap {
        val size = invariantDeviceProfile.iconBitmapSize
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        canvas.setBitmap(bitmap)
        oldBounds.set(icon.bounds)

        if (ATLEAST_OREO && icon is AdaptiveIconDrawable) {
            val offset = max(ceil(BLUR_FACTOR * size), round(size * (1 - scale) / 2)).toInt()
            icon.setBounds(offset, offset, size - offset, size - offset)
            icon.draw(canvas)
        }
        else {
            if (icon is BitmapDrawable) {
                val bmp = icon.bitmap

                if (bmp.density == Bitmap.DENSITY_NONE) {
                    icon.setTargetDensity(resourceProvider.displayMetrics)
                }
            }

            var width = size
            var height = size

            val intrinsicWidth = icon.intrinsicWidth
            val intrinsicHeight = icon.intrinsicHeight

            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                val ratio = intrinsicWidth.toFloat() / intrinsicHeight

                if (intrinsicWidth > intrinsicHeight) {
                    height = (width / ratio).toInt()
                }

                if (intrinsicHeight > intrinsicWidth) {
                    width = (height / ratio).toInt()
                }
            }

            val left = (size - width) / 2
            val top = (size - height) / 2

            icon.setBounds(left, top, left + width, top + height)
            canvas.save()
            canvas.scale(scale, scale, size.toFloat() / 2, size.toFloat() / 2)
            icon.draw(canvas)
            canvas.restore()
        }

        icon.bounds = oldBounds
        canvas.setBitmap(null)
        return bitmap
    }

    private fun normalizeAndWrapToAdaptiveIcon(drawable: Drawable, outScale: FloatArray): Drawable {
        var scale: Float
        val outBounds: RectF? = null
        var icon = drawable

        if (ATLEAST_OREO) {
            val wrapperIcon = resourceProvider
                .getDrawable(R.drawable.adaptive_icon_drawable_wrapper)?.mutate()
            val adaptiveIconDrawable = wrapperIcon as AdaptiveIconDrawable
            adaptiveIconDrawable.setBounds(0, 0, 1, 1)

            val outShape = BooleanArray(1)
            scale = iconNormalizer
                .getScale(icon, outBounds, adaptiveIconDrawable.iconMask, outShape)

            if (icon !is AdaptiveIconDrawable && !outShape[0]) {
                val fixedScaleDrawable = adaptiveIconDrawable.foreground as FixedScaleDrawable
                fixedScaleDrawable.drawable = icon
                fixedScaleDrawable.setScale(scale)
                icon = adaptiveIconDrawable
                scale = iconNormalizer
                    .getScale(icon, outBounds, null, null)
                (adaptiveIconDrawable.background as ColorDrawable).color = wrapperBackgroundColor
            }
        }
        else {
            scale = iconNormalizer.getScale(icon, outBounds, null, null)
        }

        outScale[0] = scale
        return icon
    }

    companion object {
        private const val DEFAULT_WRAPPER_BACKGROUND = Color.WHITE
        private const val BLUR_FACTOR = 0.5f / 48
        private val ATLEAST_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}