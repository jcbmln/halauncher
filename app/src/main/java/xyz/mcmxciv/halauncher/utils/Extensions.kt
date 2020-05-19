package xyz.mcmxciv.halauncher.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.view.View
import android.widget.TextView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(): ByteArray =
    ByteArrayOutputStream().use { out ->
        compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        return@use out.toByteArray()
    }

fun ByteArray.toBitmap(): Bitmap =
    ByteArrayInputStream(this).use { input ->
        return@use BitmapFactory.decodeStream(input)
    }

fun View.getSourceBounds(): Rect {
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
