package xyz.mcmxciv.halauncher.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import xyz.mcmxciv.halauncher.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.icons.IconShape

class AdaptiveIconButton(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var foreground: Bitmap
    private var background: Bitmap
    private val idp = InvariantDeviceProfile.getInstance(context)
    private val mask = Paint(Paint.ANTI_ALIAS_FLAG)
    private val foregroundRect: RectF
    private val backgroundRect: RectF

    private lateinit var drawable: Drawable

    init {
        val size = idp.iconBitmapSize
        foreground = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        foregroundPaint.shader =
            BitmapShader(foreground, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        foregroundRect = RectF(0f, 0f, idp.iconBitmapSize.toFloat(), idp.iconBitmapSize.toFloat())
        background = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        backgroundPaint.shader =
            BitmapShader(background, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        backgroundRect = RectF(0f, 0f, idp.iconBitmapSize.toFloat(), idp.iconBitmapSize.toFloat())
        mask.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawable.draw(canvas)
        //canvas.drawRect(backgroundRect, backgroundPaint)
        //canvas.drawRect(foregroundRect, foregroundPaint)
        //canvas.drawPath(IconShape.shapePath, mask)
    }

    fun setIcon(icon: AdaptiveIconDrawable) {
        drawable = icon
        background.eraseColor(Color.TRANSPARENT)
        foreground.eraseColor(Color.TRANSPARENT)
        val c = Canvas()
        icon.background?.let { rasterize(it, background, c) }
        icon.foreground?.let { rasterize(it, foreground, c) }
    }

    fun setIcon(icon: Drawable) {
        drawable = icon
    }

    private fun rasterize(drawable: Drawable, bitmap: Bitmap, canvas: Canvas) {
        drawable.setBounds(0, 0, idp.iconBitmapSize, idp.iconBitmapSize)
        canvas.setBitmap(bitmap)
        drawable.draw(canvas)
    }
}