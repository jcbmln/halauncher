package xyz.mcmxciv.halauncher.utils

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.Region
import android.graphics.RegionIterator
import android.util.Log
import androidx.annotation.ColorInt
import java.io.ByteArrayOutputStream
import java.io.IOException

object GraphicsUtils {
    private const val TAG = "GraphicsUtils"

    /**
     * Set the alpha component of {@code color} to be {@code alpha}. Unlike the support lib version,
     * it bounds the alpha in valid range instead of throwing an exception to allow for safer
     * interpolation of color animations
     */
    @ColorInt
    fun setColorAlphaBound(color: Int, alpha: Int): Int {
        val newAlpha = when {
            alpha < 0 -> 0
            alpha > 255 -> 255
            else -> alpha
        }

        return color and 0x00ffffff or (newAlpha shl 24)
    }

    /**
     * Compresses the bitmap to a byte array for serialization.
     */
    fun flattenBitmap(bitmap: Bitmap): ByteArray? {
        // Try go guesstimate how much space the icon will take when serialized
        // to avoid unnecessary allocations/copies during the write (4 bytes per pixel).
        val size = bitmap.width * bitmap.height * 4
        val out = ByteArrayOutputStream(size)

        return try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            out.toByteArray()
        } catch (e: IOException) {
            Log.w(TAG, "Could not write bitmap")
            null
        }
    }

    fun getArea(region: Region): Int {
        val iterator = RegionIterator(region)
        var area = 0
        val tempRect = Rect()

        while (iterator.next(tempRect)) {
            area += tempRect.width() * tempRect.height()
        }

        return area
    }
}