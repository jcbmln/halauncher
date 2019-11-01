/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.graphics.Bitmap

open class BitmapInfo {
    val lowResIcon: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    lateinit var icon: Bitmap
    var color = 0

    fun applyTo(info: BitmapInfo) {
        info.icon = icon
        info.color = color
    }

    fun isLowRes(): Boolean = lowResIcon == icon

    companion object {
        fun fromBitmap(bitmap: Bitmap): BitmapInfo = fromBitmap(bitmap, null)

        fun fromBitmap(bitmap: Bitmap, dominantColorExtractor: ColorExtractor?): BitmapInfo {
            val info = BitmapInfo()
            info.icon = bitmap
            info.color = dominantColorExtractor?.findDominantColorByHue(bitmap) ?: 0

            return info
        }
    }
}