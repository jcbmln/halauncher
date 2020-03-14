package xyz.mcmxciv.halauncher.icons

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShadowGenerator @Inject constructor(
    invariantDeviceProfile: InvariantDeviceProfile
) {
    private val iconSize = invariantDeviceProfile.iconBitmapSize
    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val defaultBlurMaskFilter =
        BlurMaskFilter(iconSize * BLUR_FACTOR, BlurMaskFilter.Blur.NORMAL)

    @Synchronized
    fun recreateIcon(icon: Bitmap, out: Canvas) {
        blurPaint.maskFilter = defaultBlurMaskFilter

        val offset = IntArray(2)
        val shadow = icon.extractAlpha(blurPaint, offset)

        drawPaint.alpha = AMBIENT_SHADOW_ALPHA
        out.drawBitmap(shadow, offset[0].toFloat(), offset[1].toFloat(), drawPaint)

        drawPaint.alpha = KEY_SHADOW_ALPHA
        out.drawBitmap(
            shadow,
            offset[0].toFloat(),
            offset[1].toFloat() + KEY_SHADOW_DISTANCE,
            drawPaint
        )

        drawPaint.alpha = 255
        out.drawBitmap(icon, 0f, 0f, drawPaint)
    }

    companion object {
        private const val BLUR_FACTOR = 0.5f / 48

        // Percent of actual icon size
        private const val KEY_SHADOW_DISTANCE = 1f / 48
        private const val KEY_SHADOW_ALPHA = 61
        // Percent of actual icon size
        private const val HALF_DISTANCE = 0.5f
        private const val AMBIENT_SHADOW_ALPHA = 30
    }
}