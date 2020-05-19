package xyz.mcmxciv.halauncher.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import kotlin.math.round

object BlurBuilder {
    private const val BITMAP_SCALE = 0.4f
    private const val BLUR_RADIUS = 7.5f

    fun blur(view: View): Bitmap {
        val context = view.context
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val width = round(bitmap.width * BITMAP_SCALE).toInt()
        val height = round(bitmap.height * BITMAP_SCALE).toInt()
        val inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val renderScript = RenderScript.create(context)
        val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val allocationIn = Allocation.createFromBitmap(renderScript, inputBitmap)
        val allocationOut = Allocation.createFromBitmap(renderScript, outputBitmap)

        intrinsicBlur.setRadius(BLUR_RADIUS)
        intrinsicBlur.setInput(allocationIn)
        intrinsicBlur.forEach(allocationOut)
        allocationOut.copyTo(outputBitmap)

        renderScript.destroy()

        return outputBitmap
    }
}
