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

package xyz.mcmxciv.halauncher.utilities

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.TypedValue
import android.util.Xml
import android.view.WindowManager

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.util.ArrayList
import java.util.Collections

import xyz.mcmxciv.halauncher.R
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class InvariantDeviceProfile {

    // Profile-defining invariant properties
    internal lateinit var name: String
    private var minWidthDps: Float = 0.toFloat()
    private var minHeightDps: Float = 0.toFloat()

    /**
     * Number of icons per row and column in the folder.
     */
    private var iconSize: Float = 0.toFloat()
    private var landscapeIconSize: Float = 0.toFloat()
    var iconBitmapSize: Int = 0
    var fillResIconDpi: Int = 0
    var numColumns: Int = 4
    private var iconTextSize: Float = 0.toFloat()

    constructor()

    constructor(p: InvariantDeviceProfile) : this(p.name, p.minWidthDps, p.minHeightDps, p.iconSize,
        p.landscapeIconSize, p.iconTextSize, p.numColumns)

    internal constructor(n: String, w: Float, h: Float, `is`: Float, lis: Float,
                         its: Float, nc: Int) {
        name = n
        minWidthDps = w
        minHeightDps = h
        iconSize = `is`
        landscapeIconSize = lis
        iconTextSize = its
        numColumns = nc
    }

    internal constructor(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)

        val smallestSize = Point()
        val largestSize = Point()
        display.getCurrentSizeRange(smallestSize, largestSize)

        // This guarantees that width < height
        minWidthDps = dpiFromPx(min(smallestSize.x, smallestSize.y), dm)
        minHeightDps = dpiFromPx(min(largestSize.x, largestSize.y), dm)

        val closestProfiles = findClosestDeviceProfiles(
            minWidthDps, minHeightDps, getPredefinedDeviceProfiles(context)
        )
        numColumns = closestProfiles[0].numColumns

        val interpolatedDeviceProfileOut =
            invDistWeightedInterpolate(minWidthDps, minHeightDps, closestProfiles)
        iconSize = interpolatedDeviceProfileOut.iconSize
        landscapeIconSize = interpolatedDeviceProfileOut.landscapeIconSize
        iconTextSize = interpolatedDeviceProfileOut.iconTextSize

        iconBitmapSize = pxFromDp(iconSize, dm)
        fillResIconDpi = getLauncherIconDensity(iconBitmapSize)
    }

    private fun getPredefinedDeviceProfiles(context: Context): ArrayList<InvariantDeviceProfile> {
        val profiles = ArrayList<InvariantDeviceProfile>()
        try {
            context.resources.getXml(R.xml.device_profiles).use { parser ->
                val depth = parser.depth
                var type: Int

                while (true) {
                    type = parser.next()

                    if ((type != XmlPullParser.END_TAG || parser.depth > depth)
                        && type != XmlPullParser.END_DOCUMENT ) {
                        if (type == XmlPullParser.START_TAG && "profile" == parser.name) {
                            val a = context.obtainStyledAttributes(
                                Xml.asAttributeSet(parser), R.styleable.InvariantDeviceProfile
                            )
                            val iconSize = a.getFloat(R.styleable.InvariantDeviceProfile_iconSize,
                                0f)
                            profiles.add(
                                InvariantDeviceProfile(
                                    a.getString(R.styleable.InvariantDeviceProfile_name) ?: "",
                                    a.getFloat(R.styleable.InvariantDeviceProfile_minWidthDps, 0f),
                                    a.getFloat(R.styleable.InvariantDeviceProfile_minHeightDps, 0f),
                                    iconSize,
                                    a.getFloat(
                                        R.styleable.InvariantDeviceProfile_landscapeIconSize,
                                        iconSize
                                    ),
                                    a.getFloat(R.styleable.InvariantDeviceProfile_iconTextSize, 0f),
                                    a.getInt(R.styleable.InvariantDeviceProfile_numColumns, 4)
                                )
                            )
                            a.recycle()
                        }
                    }
                    else {
                        break
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: XmlPullParserException) {
            throw RuntimeException(e)
        }

        return profiles
    }

    private fun getLauncherIconDensity(requiredSize: Int): Int {
        // Densities typically defined by an app.
        val densityBuckets = intArrayOf(
            DisplayMetrics.DENSITY_LOW,
            DisplayMetrics.DENSITY_MEDIUM,
            DisplayMetrics.DENSITY_TV,
            DisplayMetrics.DENSITY_HIGH,
            DisplayMetrics.DENSITY_XHIGH,
            DisplayMetrics.DENSITY_XXHIGH,
            DisplayMetrics.DENSITY_XXXHIGH
        )

        var density = DisplayMetrics.DENSITY_XXXHIGH
        for (i in densityBuckets.indices.reversed()) {
            val expectedSize =
                ICON_SIZE_DEFINED_IN_APP_DP * densityBuckets[i] / DisplayMetrics.DENSITY_DEFAULT
            if (expectedSize >= requiredSize) {
                density = densityBuckets[i]
            }
        }

        return density
    }


    /**
     * Returns the closest device profiles ordered by closeness to the specified width and height
     */
    // Package private visibility for testing.
    private fun findClosestDeviceProfiles(
        width: Float, height: Float, points: ArrayList<InvariantDeviceProfile>
    ): ArrayList<InvariantDeviceProfile> {

        // Sort the profiles by their closeness to the dimensions
        points.sortWith(Comparator { a, b ->
            dist(width, height, a.minWidthDps, a.minHeightDps).compareTo(
                dist(
                    width,
                    height,
                    b.minWidthDps,
                    b.minHeightDps
                )
            )
        })

        return points
    }

    // Package private visibility for testing.
    private fun invDistWeightedInterpolate(
        width: Float, height: Float,
        points: ArrayList<InvariantDeviceProfile>
    ): InvariantDeviceProfile {
        var weights = 0f

        var p = points[0]
        if (dist(width, height, p.minWidthDps, p.minHeightDps) == 0f) {
            return p
        }

        val out = InvariantDeviceProfile()
        var i = 0
        while (i < points.size && i < KNEARESTNEIGHBOR) {
            p = InvariantDeviceProfile(points[i])
            val w = weight(width, height, p.minWidthDps, p.minHeightDps, WEIGHT_POWER)
            weights += w
            out.add(p.multiply(w))
            ++i
        }
        return out.multiply(1.0f / weights)
    }

    private fun add(p: InvariantDeviceProfile) {
        iconSize += p.iconSize
        landscapeIconSize += p.landscapeIconSize
        iconTextSize += p.iconTextSize
    }

    private fun multiply(w: Float): InvariantDeviceProfile {
        iconSize *= w
        landscapeIconSize *= w
        iconTextSize *= w
        return this
    }

    private fun weight(x0: Float, y0: Float, x1: Float, y1: Float, pow: Float): Float {
        val d = dist(x0, y0, x1, y1)
        return if (d.compareTo(0f) == 0) {
            java.lang.Float.POSITIVE_INFINITY
        } else (WEIGHT_EFFICIENT / d.toDouble().pow(pow.toDouble())).toFloat()
    }

    private fun dpiFromPx(size: Int, metrics: DisplayMetrics): Float {
        val densityRatio = metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
        return size / densityRatio
    }

    private fun pxFromDp(size: Float, metrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            size, metrics
        ).roundToInt()
    }

    private fun dist(x0: Float, y0: Float, x1: Float, y1: Float): Float {
        return hypot((x1 - x0).toDouble(), (y1 - y0).toDouble()).toFloat()
    }

    companion object {
        private const val ICON_SIZE_DEFINED_IN_APP_DP = 48f

        // Constants that affects the interpolation curve between statically defined device profile
        // buckets.
        private const val KNEARESTNEIGHBOR = 3f
        private const val WEIGHT_POWER = 5f

        // used to offset float not being able to express extremely small weights in extreme cases.
        private const val WEIGHT_EFFICIENT = 100000f
    }
}