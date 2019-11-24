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

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.graphics.Paint.DITHER_FLAG
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.drawable.*
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import xyz.mcmxciv.halauncher.R
import kotlin.math.roundToInt

class IconFactory(private val context: Context,
                  private val iconBitmapSize: Int
) : AutoCloseable {
    private var colorExtractorDisabled = false
    private var wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND

    private var _normalizer: IconNormalizer? = null
    private val normalizer: IconNormalizer
        get() = _normalizer ?: IconNormalizer(context, iconBitmapSize)

    init {
        clear()
    }

    override fun close() {
        clear()
    }

    fun createIcon(icon: Drawable): Drawable {
        return normalizeAndWrapToAdaptiveIcon(icon)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun normalizeAndWrapToAdaptiveIcon(icon: Drawable): Drawable {
        var normalizedIcon = icon

        if (icon !is AdaptiveIconDrawable) {
            val wrapperIcon = context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper)!!
                .mutate()

            val dr = wrapperIcon as AdaptiveIconDrawable
            dr.setBounds(0, 0, 1, 1)

            val scale = normalizer.getScale(icon)
            val fsd = dr.foreground as FixedScaleDrawable
            fsd.drawable = icon
            fsd.setScale(scale)
            normalizedIcon = dr

            (dr.background as ColorDrawable).color = wrapperBackgroundColor
        }

        return normalizedIcon
    }

    private fun clear() {
        wrapperBackgroundColor = DEFAULT_WRAPPER_BACKGROUND
        colorExtractorDisabled = false
    }

    companion object {
        private const val DEFAULT_WRAPPER_BACKGROUND = Color.WHITE
    }
}