/*
 *  Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package xyz.mcmxciv.halauncher.views

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.RequiresApi
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.icons.IconShape
import xyz.mcmxciv.halauncher.utils.Utilities
import kotlin.math.roundToInt

/**
 * A custom view for rendering [AdaptiveIconDrawable]s.
 *
 * Note that this is a prototype implementation; I do not recommend using any of this code in
 * production. The technique employed holds [Bitmap]s of both foreground & background layers, then
 * renders masked versions using [BitmapShader]s.
 */
@RequiresApi(Build.VERSION_CODES.O)
class AdaptiveIconView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs, R.attr.adaptiveIconViewStyle) {

    private val foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val layerSize: Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        72f,
        context.resources.displayMetrics
    ).roundToInt()
    private val iconSize: Int
    private val layerCenter: Float
    private val offset: Int
    private val background: Bitmap
    private val foreground: Bitmap
    private lateinit var text: String

    init {
//        val prefs = LauncherApplication.instance.launcherSettings
        iconSize = (layerSize / (1 + 2 * AdaptiveIconDrawable.getExtraInsetFraction())).toInt()
        layerCenter = iconSize / 2f
        offset = (layerSize - iconSize) / 2

        background = Bitmap.createBitmap(layerSize, layerSize, Bitmap.Config.ARGB_8888)
        backgroundPaint.shader = BitmapShader(background, CLAMP, CLAMP)
        foreground = Bitmap.createBitmap(layerSize, layerSize, Bitmap.Config.ARGB_8888)
        foregroundPaint.shader = BitmapShader(foreground, CLAMP, CLAMP)

        textPaint.textSize = Utilities.pxFromSp(12f, context.resources.displayMetrics).toFloat()
        textPaint.color = context.getColor(R.color.primary_background_color)
//        textPaint.color = if (prefs.transparentBackground)
//            context.getColor(R.color.colorBackground)
//            else context.getColor(R.color.colorForeground)

//        IconShape.setShape(prefs.iconShapeType, layerCenter)
    }

    fun setIcon(icon: AdaptiveIconDrawable) {
        background.eraseColor(Color.TRANSPARENT)
        foreground.eraseColor(Color.TRANSPARENT)
        val c = Canvas()
        icon.background?.let { rasterize(it, background, c) }
        icon.foreground?.let { rasterize(it, foreground, c) }
    }

    fun setText(value: String) {
        text = value
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(layerSize, layerSize + textPaint.textSize.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val textLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, layerSize)
            .setAlignment(Layout.Alignment.ALIGN_CENTER).build()
        val offsetX = offset.toFloat()
        val offsetY = iconSize.toFloat()

        canvas.run {
            save()
            translate(offsetX, 0f)
            drawPath(IconShape.shapePath, backgroundPaint)
            drawPath(IconShape.shapePath, foregroundPaint)
            translate(-offsetX, offsetY)
            textLayout.draw(canvas)
            restore()
        }
    }

    private fun rasterize(drawable: Drawable, bitmap: Bitmap, canvas: Canvas) {
        drawable.setBounds(-offset, -offset, layerSize - offset, layerSize - offset)
        canvas.setBitmap(bitmap)
        drawable.draw(canvas)
    }
}
