package xyz.mcmxciv.halauncher.icons

import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import timber.log.Timber
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.device.DeviceProfile
import xyz.mcmxciv.halauncher.di.AppScope
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import xyz.mcmxciv.halauncher.views.FixedScaleDrawable
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.round

@AppScope
class IconFactory @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val packageManager: PackageManager,
    private val deviceProfile: DeviceProfile,
    private val launcherApps: LauncherApps,
    private val iconNormalizer: IconNormalizer
) {
    private var wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND
    private val canvas = Canvas()
    private val oldBounds = Rect()

    init {
        canvas.drawFilter = PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG)
    }

    fun getIcon(launcherActivityInfo: LauncherActivityInfo): Bitmap {
        val drawable = getDrawable(launcherActivityInfo)
                ?: launcherActivityInfo.getIcon(deviceProfile.appIconDpi)
        return createIconBitmap(drawable)
    }

    fun getShortcutIcon(shortcutInfo: ShortcutInfo): Bitmap? {
        return launcherApps.getShortcutIconDrawable(
            shortcutInfo,
            deviceProfile.shortcutIconBitmapSize
        )?.toBitmap(
            deviceProfile.shortcutIconBitmapSize,
            deviceProfile.shortcutIconBitmapSize
        )
    }

    private fun getDrawable(launcherActivityInfo: LauncherActivityInfo): Drawable? {
        val iconRes = launcherActivityInfo.applicationInfo.icon
        val density = deviceProfile.appIconDpi

        return if (density != 0 && iconRes != 0) {
            try {
                packageManager
                    .getResourcesForApplication(launcherActivityInfo.applicationInfo)
                    .getDrawableForDensity(iconRes, density, null)
            } catch (ex: PackageManager.NameNotFoundException) {
                Timber.e(ex)
                null
            } catch (ex: Resources.NotFoundException) {
                Timber.e(ex)
                null
            }
        } else null
    }

    private fun createIconBitmap(drawable: Drawable): Bitmap {
        val scale = FloatArray(1)
        val icon = getNormalizedAdaptiveIcon(drawable, scale)
        val size = deviceProfile.iconBitmapSize
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        canvas.setBitmap(bitmap)
        oldBounds.set(icon.bounds)

        if (icon is AdaptiveIconDrawable) {
            val offset = max(
                ceil(BLUR_FACTOR * size),
                round(size * (1 - scale[0]) / 2)
            ).toInt()
            icon.setBounds(offset, offset, size - offset, size - offset)
            icon.draw(canvas)
        } else {
            if (icon is BitmapDrawable && icon.bitmap.density == Bitmap.DENSITY_NONE) {
                icon.setTargetDensity(resourceProvider.displayMetrics)
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

            val left = (size / width) / 2
            val top = (size / height) / 2

            icon.setBounds(left, top, left + width, top + height)
            canvas.save()
            canvas.scale(scale[0], scale[0], size.toFloat() / 2, size.toFloat() / 2)
            icon.draw(canvas)
            canvas.restore()
        }

        icon.bounds = oldBounds
        canvas.setBitmap(null)
        return bitmap
    }

    private fun getNormalizedAdaptiveIcon(drawable: Drawable, outScale: FloatArray): Drawable {
        var scale: Float
        val outBounds: RectF? = null
        var icon = drawable.mutate()

        val wrapperIcon = resourceProvider
            .getDrawable(R.drawable.adaptive_icon_drawable_wrapper)?.mutate()
        val adaptiveIconDrawable = wrapperIcon as AdaptiveIconDrawable
        adaptiveIconDrawable.setBounds(0, 0, 1, 1)

        val outShape = BooleanArray(1)
        scale = iconNormalizer.getScale(icon, outBounds, adaptiveIconDrawable.iconMask, outShape)

        if (icon !is AdaptiveIconDrawable && !outShape[0]) {
            val fixedScaleDrawable = adaptiveIconDrawable.foreground as FixedScaleDrawable
            fixedScaleDrawable.drawable = icon
            fixedScaleDrawable.setScale(scale)
            icon = adaptiveIconDrawable
            scale = iconNormalizer
                .getScale(icon, outBounds, null, null)
            adaptiveIconDrawable.background.setTint(wrapperBackgroundColor)
        }

        outScale[0] = scale
        return icon
    }

    companion object {
        private const val DEFAULT_WRAPPER_BACKGROUND = Color.WHITE
        private const val BLUR_FACTOR = 0.5F / 48
    }
}
