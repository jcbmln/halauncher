package xyz.mcmxciv.halauncher.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.view.View
import android.widget.TextView
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

fun Bitmap.toByteArray(): ByteArray? {
    val size = width * height * 4
    return try {
        ByteArrayOutputStream(size).use { out ->
            compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            return@use out.toByteArray()
        }
    } catch (ex: IOException) {
        Timber.e(ex)
        return null
    }
}

fun ByteArray.toBitmap(): Bitmap? {
    return try {
        ByteArrayInputStream(this).use { input ->
            return@use BitmapFactory.decodeStream(input)
        }
    } catch (ex: IOException) {
        Timber.e(ex)
        return null
    }
}

fun View.getBounds(): Rect {
    val position = IntArray(2)
    getLocationOnScreen(position)
    return Rect(
        position[0],
        position[1],
        position[0] + width,
        position[1] + height
    )
}

val TextView.textString: String
    get() = text.toString()