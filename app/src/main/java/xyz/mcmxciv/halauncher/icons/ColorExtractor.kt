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
import android.graphics.Color
import android.util.SparseArray
import java.util.*
import kotlin.math.sqrt

class ColorExtractor {
    private val numSamples = 20
    private val tempHsv = FloatArray(3)
    private val tempHueScoreHistogram = FloatArray(360)
    private val tempPixels = IntArray(numSamples)
    private val tempRgbScores = SparseArray<Float>()

    /**
     * This picks a dominant color, looking for high-saturation, high-value, repeated hues.
     * @param bitmap The bitmap to scan
     */
    fun findDominantColorByHue(bitmap: Bitmap) = findDominantColorByHue(bitmap, numSamples)

    /**
     * This picks a dominant color, looking for high-saturation, high-value, repeated hues.
     * @param bitmap The bitmap to scan
     */
    fun findDominantColorByHue(bitmap: Bitmap, samples: Int): Int {
        val height = bitmap.height
        val width = bitmap.width
        var sampleStride: Int = sqrt(((height * width) / samples).toDouble()).toInt()

        if (sampleStride < 1) {
            sampleStride = 1
        }

        // This is an out-param, for getting the hsv values for an rgb
        val hsv = tempHsv
        Arrays.fill(hsv, 0F)

        // First get the best hue, by creating a histogram over 360 hue buckets,
        // where each pixel contributes a score weighted by saturation, value, and alpha.
        val hueScoreHistogram = tempHueScoreHistogram
        Arrays.fill(hueScoreHistogram , 0F)
        var highScore = -1F
        var bestHue = -1

        val pixels = tempPixels
        Arrays.fill(pixels, 0)
        var pixelCount = 0

        var y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val argb = bitmap.getPixel(x, y)
                val alpha = 0xFF and (argb shr 24)

                if (alpha < 0x80) {
                    // Drop mostly-transparent pixels.
                    x += sampleStride
                    continue
                }

                // Remove the alpha channel.
                val rgb = argb or -0x1000000
                Color.colorToHSV(rgb, hsv)

                // Bucket colors by the 360 integer hues.
                val hue = hsv[0].toInt()

                if (hue < 0 || hue >= hueScoreHistogram.size) {
                    // Defensively avoid array bounds violations.
                    x += sampleStride
                    continue
                }

                if (pixelCount < samples) {
                    pixels[pixelCount++] = rgb
                }

                val score = hsv[1] * hsv[2]
                hueScoreHistogram[hue] += score

                if (hueScoreHistogram[hue] > highScore) {
                    highScore = hueScoreHistogram[hue]
                    bestHue = hue
                }

                x += sampleStride
            }

            y += sampleStride
        }

        val rgbScores = tempRgbScores
        rgbScores.clear()
        var bestColor = 0xff000000.toInt()
        highScore = -1F

        // Go back over the RGB colors that match the winning hue,
        // creating a histogram of weighted s*v scores, for up to 100*100 [s,v] buckets.
        // The highest-scoring RGB color wins.
        var i = 0
        while (i < pixelCount) {
            val rgb = pixels[i]
            Color.colorToHSV(rgb, hsv)
            val hue = hsv[0].toInt()

            if (hue == bestHue) {
                val s = hsv[1]
                val v = hsv[2]
                val bucket = ((s * 100) + (v * 10000)).toInt()

                // Score by cumulative saturation * value.
                val score = s * v
                val oldTotal = rgbScores[bucket]
                val newTotal = if (oldTotal == null) score else oldTotal + score

                rgbScores.put(bucket, newTotal)

                if (newTotal > highScore) {
                    highScore = newTotal
                    // All the colors in the winning bucket are very similar. Last in wins.
                    bestColor = rgb
                }
            }

            i++
        }

        return bestColor
    }
}