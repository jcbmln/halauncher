package xyz.mcmxciv.halauncher.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import xyz.mcmxciv.halauncher.utilities.UserPreferences
import kotlin.math.roundToInt

open class ViewPagerFragment
internal constructor(drawable: Drawable?): Fragment() {
    private val wallpaper = drawable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackground(view)
    }

    private fun setBackground(view: View) {
        if (UserPreferences.canGetWallpaper) {
            view.background = if (UserPreferences.blurBackground)
                blurBackground(wallpaper) else wallpaper
        }
    }

    private fun blurBackground(drawable: Drawable?): Drawable? {
        val bitmapScale = 0.4f
        val blurRadius = 20f
        val bitmap = drawable?.toBitmap()

        if (bitmap is Bitmap) {
            val width = (bitmap.width * bitmapScale).roundToInt()
            val height = (bitmap.height * bitmapScale).roundToInt()

            val input = Bitmap.createScaledBitmap(bitmap, width, height, false)
            val output = Bitmap.createBitmap(input)

            val rs = RenderScript.create(context)
            val intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            val tmpIn = Allocation.createFromBitmap(rs, input)
            val tmpOut = Allocation.createFromBitmap(rs, output)

            intrinsicBlur.setRadius(blurRadius)
            intrinsicBlur.setInput(tmpIn)
            intrinsicBlur.forEach(tmpOut)

            tmpOut.copyTo(output)

            return output.toDrawable(resources)
        }

        return null
    }
}
