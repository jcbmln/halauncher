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

package xyz.mcmxciv.halauncher

import android.appwidget.AppWidgetHostView
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.PatternMatcher
import android.text.TextUtils
import android.util.*
import android.view.WindowManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import xyz.mcmxciv.halauncher.icons.IconShape
import xyz.mcmxciv.halauncher.utils.ConfigMonitor

import java.util.ArrayList
import xyz.mcmxciv.halauncher.utils.Utilities
import java.io.IOException
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class InvariantDeviceProfile {
    var numColumns: Int = 0
    var iconSize: Float = 0.toFloat()
    var iconShapePath: String = ""
    var landscapeIconSize: Float = 0.toFloat()
    var iconBitmapSize: Int = 0
    var fillResIconDpi: Int = 0
    var iconTextSize: Float = 0.toFloat()

    lateinit var landscapeProfile: DeviceProfile
    lateinit var portraitProfile: DeviceProfile
//
//    var defaultWallpaperSize: Point

    private val changeListeners = ArrayList<OnIDPChangeListener>()
    private var configMonitor: ConfigMonitor? = null
    private var overlayMonitor: OverlayMonitor? = null

    private constructor(p: InvariantDeviceProfile) {
        numColumns = p.numColumns
        iconSize = p.iconSize
        iconShapePath = p.iconShapePath
        landscapeIconSize = p.landscapeIconSize
        iconTextSize = p.iconTextSize
        overlayMonitor = p.overlayMonitor
    }

    private constructor(context: Context) {
        initGrid(context, Utilities.getPrefs(context).getString(KEY_IDP_GRID_NAME, null))
        configMonitor = ConfigMonitor(context, ::onConfigChanged)
        overlayMonitor = OverlayMonitor(context)
    }

    /**
     * This constructor should NOT have any monitors by design.
     */
    constructor(context: Context, gridName: String) {
        val newName = initGrid(context, gridName)
        require(newName != null && newName == gridName) { "Unknown grid name" }
    }

    private fun initGrid(context: Context, gridName: String?): String? {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)

        val smallestSize = Point()
        val largestSize = Point()
        display.getCurrentSizeRange(smallestSize, largestSize)

        val allOptions = getPredefinedDeviceProfiles(context, gridName)
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
        numColumns = closestProfile?.numColumns ?: numColumns

        if (closestProfile?.name != gridName) {
            Utilities.getPrefs(context).edit()
                .putString(KEY_IDP_GRID_NAME, closestProfile?.name).apply()
        }

        iconSize = interpolatedDisplayOption.iconSize
        iconShapePath = getIconShapePath(context)
        landscapeIconSize = interpolatedDisplayOption.landscapeIconSize
        iconBitmapSize = Utilities.pxFromDp(iconSize, dm)
        iconTextSize = interpolatedDisplayOption.iconTextSize
        fillResIconDpi = getLauncherIconDensity(iconBitmapSize)

        val realSize = Point()
        display.getRealSize(realSize)
        // The real size never changes. smallSide and largeSide will remain the
        // same in any orientation.
        val smallSide = min(realSize.x, realSize.y)
        val largeSide = max(realSize.x, realSize.y)

        landscapeProfile = DeviceProfile(
            context, this, smallestSize, largestSize,
            largeSide, smallSide,
            isLandscape = true,
            isMultiWindowMode = false
        )
        portraitProfile = DeviceProfile(
            context, this, smallestSize, largestSize,
            smallSide, largeSide,
            isLandscape = false,
            isMultiWindowMode = false
        )

        return closestProfile?.name
    }

    fun addOnChangeListener(listener: OnIDPChangeListener) {
        changeListeners.add(listener)
    }

    fun removeOnChangeListener(listener: OnIDPChangeListener) {
        changeListeners.remove(listener)
    }

    private fun killProcess(context: Context) {
        Log.e("ConfigMonitor", "restarting launcher")
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun verifyConfigChangedInBackground(context: Context) {
        val savedIconMaskPath = Utilities.getDevicePrefs(context).getString(KEY_ICON_PATH_REF, "")
        // Good place to check if grid size changed in themepicker when launcher was dead.
        if (savedIconMaskPath?.isEmpty() == true) {
            Utilities.getDevicePrefs(context).edit().putString(KEY_ICON_PATH_REF, getIconShapePath(context))
                .apply()
        } else if (savedIconMaskPath != getIconShapePath(context)) {
            Utilities.getDevicePrefs(context).edit().putString(KEY_ICON_PATH_REF, getIconShapePath(context))
                .apply()
            apply(context, CHANGE_FLAG_ICON_PARAMS)
        }
    }

    private fun onConfigChanged(context: Context) {
        // Config changes, what shall we do?
        val oldProfile = InvariantDeviceProfile(this)

        initGrid(context, null)

        var changeFlags = 0

        if (iconSize != oldProfile.iconSize || iconBitmapSize != oldProfile.iconBitmapSize ||
            iconShapePath != oldProfile.iconShapePath
        ) {
            changeFlags = changeFlags or CHANGE_FLAG_ICON_PARAMS
        }
        if (iconShapePath != oldProfile.iconShapePath) {
            IconShape.init(context)
        }

        apply(context, changeFlags)
    }

    private fun apply(context: Context, changeFlags: Int) {
        // Create a new config monitor
//        configMonitor!!.unregister()
//        configMonitor = ConfigMonitor(context, ???({ this.onConfigChanged(it) }))

        for (listener in changeListeners) {
            listener.onIdpChanged(changeFlags, this)
        }
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

    fun getDeviceProfile(context: Context): DeviceProfile {
        return if (
            context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        )
            landscapeProfile
        else
            portraitProfile
    }

    interface OnIDPChangeListener {
        fun onIdpChanged(changeFlags: Int, profile: InvariantDeviceProfile)
    }


    class GridOption(context: Context, attrs: AttributeSet) {
        val name: String
        val numColumns: Int

        private val defaultLayoutId: Int

        init {
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.GridDisplayOption
            )
            name = a.getString(R.styleable.GridDisplayOption_name) ?: ""
            numColumns = a.getInt(R.styleable.GridDisplayOption_numColumns, 0)

            defaultLayoutId = a.getResourceId(
                R.styleable.GridDisplayOption_defaultLayoutId, 0
            )

            a.recycle()
        }

        companion object {
            const val TAG_NAME = "GridOption"
        }
    }

    private class DisplayOption {
        val grid: GridOption?
        private val name: String?
        val minWidthDps: Float
        val minHeightDps: Float
        val canBeDefault: Boolean

        var iconSize: Float = 0.toFloat()
        var landscapeIconSize: Float = 0.toFloat()
        var iconTextSize: Float = 0.toFloat()

        constructor() {
            grid = null
            name = ""
            minWidthDps = 0f
            minHeightDps = 0f
            canBeDefault = false
        }

        constructor(grid: GridOption, context: Context, attrs: AttributeSet) {
            this.grid = grid

            val a = context.obtainStyledAttributes(
                attrs, R.styleable.ProfileDisplayOption
            )

            name = a.getString(R.styleable.ProfileDisplayOption_name)
            minWidthDps = a.getFloat(R.styleable.ProfileDisplayOption_minWidthDps, 0f)
            minHeightDps = a.getFloat(R.styleable.ProfileDisplayOption_minHeightDps, 0f)
            canBeDefault = a.getBoolean(
                R.styleable.ProfileDisplayOption_canBeDefault, false
            )

            iconSize = a.getFloat(R.styleable.ProfileDisplayOption_iconImageSize, 0f)
            landscapeIconSize = a.getFloat(
                R.styleable.ProfileDisplayOption_landscapeIconSize,
                iconSize
            )
            iconTextSize = a.getFloat(R.styleable.ProfileDisplayOption_iconTextSize, 0f)
            a.recycle()
        }

        fun multiply(w: Float): DisplayOption {
            iconSize *= w
            landscapeIconSize *= w
            iconTextSize *= w
            return this
        }

        fun add(p: DisplayOption): DisplayOption {
            iconSize += p.iconSize
            landscapeIconSize += p.landscapeIconSize
            iconTextSize += p.iconTextSize
            return this
        }
    }

    private inner class OverlayMonitor internal constructor(context: Context) :
        BroadcastReceiver() {

        private val actionOverlayChanged = "android.intent.action.OVERLAY_CHANGED"

        init {
            context.registerReceiver(this, getPackageFilter("android", actionOverlayChanged))
        }

        override fun onReceive(context: Context, intent: Intent) {
            onConfigChanged(context)
        }
    }

    companion object {
        private const val TAG = "InvariantDeviceProfile"
        private const val KEY_IDP_GRID_NAME = "idp_grid_name"

        private const val ICON_SIZE_DEFINED_IN_APP_DP = 48f

        //const val CHANGE_FLAG_GRID = 1 shl 0
        const val CHANGE_FLAG_ICON_PARAMS = 1 shl 1

        const val KEY_ICON_PATH_REF = "pref_icon_shape_path"

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
                Log.e(TAG, "Icon mask res identifier failed to retrieve.")
                return ""
            }
            return context.resources.getString(CONFIG_ICON_MASK_RES_ID)
        }

        private fun getPredefinedDeviceProfiles(
            context: Context,
            gridName: String?
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
            return if (d.compareTo(0f) == 0) {
                Float.POSITIVE_INFINITY
            } else (WEIGHT_EFFICIENT / d.toDouble().pow(pow.toDouble())).toFloat()
        }

        /**
         * Creates an intent filter to listen for actions with a specific package in the data field.
         */
        private fun getPackageFilter(pkg: String, vararg actions: String): IntentFilter {
            val packageFilter = IntentFilter()
            for (action in actions) {
                packageFilter.addAction(action)
            }
            packageFilter.addDataScheme("package")
            packageFilter.addDataSchemeSpecificPart(pkg, PatternMatcher.PATTERN_LITERAL)
            return packageFilter
        }

        /**
         * As a ratio of screen height, the total distance we want the parallax effect to span
         * horizontally
         */
        private fun wallpaperTravelToScreenWidthRatio(width: Int, height: Int): Float {
            val aspectRatio = width / height.toFloat()

            // At an aspect ratio of 16/10, the wallpaper parallax effect should span 1.5 * screen width
            // At an aspect ratio of 10/16, the wallpaper parallax effect should span 1.2 * screen width
            // We will use these two data points to extrapolate how much the wallpaper parallax effect
            // to span (ie travel) at any aspect ratio:

            val aspectRatioLandscape = 16 / 10f
            val aspectRatioPortrait = 10 / 16f
            val wallpaperWidthToScreenRatioLandscape = 1.5f
            val wallpaperWidthToScreenRatioPortrait = 1.2f

            // To find out the desired width at different aspect ratios, we use the following two
            // formulas, where the coefficient on x is the aspect ratio (width/height):
            //   (16/10)x + y = 1.5
            //   (10/16)x + y = 1.2
            // We solve for x and y and end up with a final formula:
            val x =
                (wallpaperWidthToScreenRatioLandscape - wallpaperWidthToScreenRatioPortrait) /
                (aspectRatioLandscape - aspectRatioPortrait)
            val y = wallpaperWidthToScreenRatioPortrait - x * aspectRatioPortrait
            return x * aspectRatio + y
        }
    }
}