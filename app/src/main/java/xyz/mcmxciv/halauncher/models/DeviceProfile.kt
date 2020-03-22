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

package xyz.mcmxciv.halauncher.models

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Xml
import android.view.WindowManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utils.Utilities
import java.io.IOException
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Comparator
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.pow

@Singleton
class DeviceProfile @Inject constructor(context: Context) {
    var appDrawerColumns: Int = 0
    var iconTextSize: Float = 0f
    private var iconShapePath: String = ""
    var iconBitmapSize: Int = 0
    var shortcutBitmapSize: Int = 0
    var appIconDpi: Int = 0
    var shortcutIconDpi: Int = 0
    private val isTablet: Boolean = context.resources.getBoolean(R.bool.is_tablet)
    private val isLargeTablet: Boolean = context.resources.getBoolean(R.bool.is_large_tablet)
    private val isPhone: Boolean

    init {
        isPhone = !(isTablet || isLargeTablet)
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)

        val smallestSize = Point()
        val largestSize = Point()
        display.getCurrentSizeRange(smallestSize, largestSize)

        val allOptions = getPredefinedDeviceProfiles(context, null)

        // This guarantees that width < height
        val minWidthDps = Utilities.dpiFromPx(min(smallestSize.x, smallestSize.y), dm)
        val minHeightDps = Utilities.dpiFromPx(min(largestSize.x, largestSize.y), dm)

        // Sort the profiles based on the closeness to the device size
        allOptions.sortWith(Comparator { a, b ->
            dist(minWidthDps, minHeightDps, a.minWidthDps, a.minHeightDps).compareTo(
                dist(minWidthDps, minHeightDps, b.minWidthDps, b.minHeightDps)
            )
        })

        val interpolatedDisplayOption =
            invDistWeightedInterpolate(minWidthDps, minHeightDps, allOptions)

        val closestProfile = allOptions[0].grid
        appDrawerColumns = closestProfile?.numColumns ?: appDrawerColumns

        iconShapePath = getIconShapePath(context)
        iconBitmapSize = Utilities.pxFromDp(interpolatedDisplayOption.iconImageSize, dm)
        shortcutBitmapSize = Utilities.pxFromDp(interpolatedDisplayOption.shortcutImageSize, dm)

        iconTextSize = interpolatedDisplayOption.iconTextSize
        appIconDpi = getLauncherIconDensity(iconBitmapSize)
        shortcutIconDpi = getLauncherIconDensity(shortcutBitmapSize)
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

    class GridOption(context: Context, attrs: AttributeSet) {
        val name: String
        val numColumns: Int

        init {
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.GridDisplayOption
            )
            name = a.getString(R.styleable.GridDisplayOption_name) ?: ""
            numColumns = a.getInt(R.styleable.GridDisplayOption_numColumns, 0)

            a.recycle()
        }

        companion object {
            const val TAG_NAME = "grid-option"
        }
    }

    private class DisplayOption {
        val grid: GridOption?
        private val name: String?
        val minWidthDps: Float
        val minHeightDps: Float
        val canBeDefault: Boolean

        var iconImageSize = 0f
        var iconTextSize = 0f
        var shortcutImageSize = 0f

        constructor() {
            grid = null
            name = ""
            minWidthDps = 0f
            minHeightDps = 0f
            canBeDefault = false
        }

        constructor(grid: GridOption, context: Context, attrs: AttributeSet) {
            this.grid = grid

            val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileDisplayOption)

            name = a.getString(R.styleable.ProfileDisplayOption_name)
            minWidthDps = a.getFloat(R.styleable.ProfileDisplayOption_minWidthDps, 0f)
            minHeightDps = a.getFloat(R.styleable.ProfileDisplayOption_minHeightDps, 0f)
            canBeDefault = a.getBoolean(
                R.styleable.ProfileDisplayOption_canBeDefault, false
            )

            iconImageSize = a.getFloat(R.styleable.ProfileDisplayOption_iconImageSize, 0f)
            iconTextSize = a.getFloat(R.styleable.ProfileDisplayOption_iconTextSize, 0f)
            shortcutImageSize = a.getFloat(R.styleable.ProfileDisplayOption_shortcutImageSize, 0f)
            a.recycle()
        }

        fun multiply(w: Float): DisplayOption {
            iconImageSize *= w
            iconTextSize *= w
            shortcutImageSize *= w
            return this
        }

        fun add(p: DisplayOption): DisplayOption {
            iconImageSize += p.iconImageSize
            iconTextSize += p.iconTextSize
            shortcutImageSize += p.shortcutImageSize
            return this
        }
    }

    companion object {
        private const val ICON_SIZE_DEFINED_IN_APP_DP = 48f

        // Constants that affects the interpolation curve between statically defined device profile
        // buckets.
        private const val K_NEAREST_NEIGHBOR = 3f
        private const val WEIGHT_POWER = 5f

        // used to offset float not being able to express extremely small weights in extreme cases.
        private const val WEIGHT_EFFICIENT = 100000f

        private val CONFIG_ICON_MASK_RES_ID = Resources.getSystem().getIdentifier(
            "config_icon_mask", "string", "android"
        )

        /**
         * Retrieve system defined or RRO overriden icon shape.
         */
        private fun getIconShapePath(context: Context): String {
            if (CONFIG_ICON_MASK_RES_ID == 0) {
                Timber.e("Icon mask res identifier failed to retrieve.")
                return ""
            }
            return context.resources.getString(CONFIG_ICON_MASK_RES_ID)
        }

        private fun getPredefinedDeviceProfiles(
            context: Context, gridName: String?
        ): ArrayList<DisplayOption> {
            val profiles = ArrayList<DisplayOption>()
            try {
                context.resources.getXml(R.xml.device_profiles).use { parser ->
                    val depth = parser.depth
                    var type: Int = parser.next()
                    while ((type != XmlPullParser.END_TAG || parser.depth > depth) &&
                        type != XmlPullParser.END_DOCUMENT
                    ) {
                        if (type == XmlPullParser.START_TAG && GridOption.TAG_NAME == parser.name) {
                            val gridOption = GridOption(context, Xml.asAttributeSet(parser))
                            val displayDepth = parser.depth

                            type = parser.next()
                            while ((type != XmlPullParser.END_TAG || parser.depth > displayDepth) &&
                                type != XmlPullParser.END_DOCUMENT
                            ) {
                                if (type == XmlPullParser.START_TAG &&
                                    "display-option" == parser.name
                                ) {
                                    profiles.add(
                                        DisplayOption(
                                            gridOption, context, Xml.asAttributeSet(parser)
                                        )
                                    )
                                }

                                type = parser.next()
                            }
                        }

                        type = parser.next()
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            } catch (e: XmlPullParserException) {
                throw RuntimeException(e)
            }

            val filteredProfiles = ArrayList<DisplayOption>()
            if (!TextUtils.isEmpty(gridName)) {
                for (option in profiles) {
                    if (gridName == option.grid!!.name) {
                        filteredProfiles.add(option)
                    }
                }
            }
            if (filteredProfiles.isEmpty()) {
                // No grid found, use the default options
                for (option in profiles) {
                    if (option.canBeDefault) {
                        filteredProfiles.add(option)
                    }
                }
            }
            if (filteredProfiles.isEmpty()) {
                throw RuntimeException("No display option with canBeDefault=true")
            }
            return filteredProfiles
        }

        private fun dist(x0: Float, y0: Float, x1: Float, y1: Float): Float {
            return hypot((x1 - x0).toDouble(), (y1 - y0).toDouble()).toFloat()
        }

        private fun invDistWeightedInterpolate(
            width: Float, height: Float,
            points: ArrayList<DisplayOption>
        ): DisplayOption {
            var weights = 0f

            var p = points[0]
            if (dist(width, height, p.minWidthDps, p.minHeightDps) == 0f) {
                return p
            }

            val out = DisplayOption()
            var i = 0

            while (i < points.size && i < K_NEAREST_NEIGHBOR) {
                p = points[i]

                val w = weight(width, height, p.minWidthDps, p.minHeightDps, WEIGHT_POWER)
                weights += w
                out.add(DisplayOption().add(p).multiply(w))
                ++i
            }

            return out.multiply(1.0f / weights)
        }

        private fun weight(x0: Float, y0: Float, x1: Float, y1: Float, pow: Float): Float {
            val d = dist(x0, y0, x1, y1)

            return if (d.compareTo(0f) == 0) Float.POSITIVE_INFINITY
            else (WEIGHT_EFFICIENT / d.toDouble().pow(pow.toDouble())).toFloat()
        }
    }
}