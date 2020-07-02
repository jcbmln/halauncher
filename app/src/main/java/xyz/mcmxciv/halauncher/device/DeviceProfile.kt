package xyz.mcmxciv.halauncher.device

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Point
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Xml
import android.view.WindowManager
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utils.Utilities
import java.io.IOException
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.pow

@Singleton
class DeviceProfile @Inject constructor(@ApplicationContext context: Context) {
    val appDrawerColumns: Int
    var iconTextSize = 0f
    var iconBitmapSize = 0
    var shortcutIconBitmapSize = 0
    var appIconDpi = 0
    var shortcutIconDpi = 0

    private var iconShapePath = ""
    private val isTablet = context.resources.getBoolean(R.bool.is_tablet)
    private val isLargeTablet = context.resources.getBoolean(R.bool.is_large_tablet)
    private val isPhone = !(isTablet || isLargeTablet)

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)

        val smallestSize = Point()
        val largestSize = Point()
        display.getCurrentSizeRange(smallestSize, largestSize)

        val minWidthDps = Utilities.dpiFromPx(min(smallestSize.x, smallestSize.y), dm)
        val minHeightDps = Utilities.dpiFromPx(min(largestSize.x, largestSize.y), dm)
        val profiles = getPredefinedDeviceProfiles(
            context,
            null
        ).sortedWith(Comparator { a, b ->
            val firstDist = dist(minWidthDps, minHeightDps, a.minWidthDps, a.minHeightDps)
            val secondDist = dist(minWidthDps, minHeightDps, b.minWidthDps, b.minHeightDps)
            firstDist.compareTo(secondDist)
        })

        val interpolatedDisplayOption =
            getInterpolatedDisplayOption(minWidthDps, minHeightDps, profiles)
        val gridOption = profiles[0].gridOption

        appDrawerColumns = gridOption?.numColumns
            ?: context.resources.getInteger(R.integer.default_app_drawer_columns)

        iconShapePath = getIconShapePath(context)
        iconBitmapSize = Utilities.pxFromDpi(interpolatedDisplayOption.iconImageSize, dm)
        shortcutIconBitmapSize = Utilities
            .pxFromDpi(interpolatedDisplayOption.shortcutImageSize, dm)
        iconTextSize = interpolatedDisplayOption.iconTextSize
        appIconDpi = getLauncherIconDensity(iconBitmapSize)
        shortcutIconDpi = getLauncherIconDensity(shortcutIconBitmapSize)
    }

    private fun getPredefinedDeviceProfiles(
        context: Context,
        gridName: String?
    ): List<DisplayOption> {
        val profiles = mutableListOf<DisplayOption>()

        try {
            context.resources.getXml(R.xml.device_profiles).use { parser ->
                parseProfiles(parser, GridOption.TAG_NAME) {
                    val gridOption = GridOption(context, Xml.asAttributeSet(parser))
                    parseProfiles(parser, DisplayOption.TAG_NAME) {
                        profiles.add(DisplayOption(context, gridOption, Xml.asAttributeSet(parser)))
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: XmlPullParserException) {
            throw RuntimeException(e)
        }

        val filteredProfiles = mutableListOf<DisplayOption>()
        if (!TextUtils.isEmpty(gridName)) {
            for (profile in profiles) {
                if (gridName == profile.gridOption!!.name) {
                    filteredProfiles.add(profile)
                }
            }
        }

        if (filteredProfiles.isEmpty()) {
            for (profile in profiles) {
                if (profile.canBeDefault) {
                    filteredProfiles.add(profile)
                }
            }
        }

        if (filteredProfiles.isEmpty()) {
            throw RuntimeException("No display option with canBeDefault=true")
        }

        return filteredProfiles
    }

    private fun getInterpolatedDisplayOption(
        width: Float,
        height: Float,
        points: List<DisplayOption>
    ): DisplayOption {
        var totalWeight = 0f

        if (dist(width, height, points[0].minWidthDps, points[0].minHeightDps) == 0f) {
            return points[0]
        }

        val out = DisplayOption()

        for (point in points) {
            val weight = weight(width, height, point.minWidthDps, point.minHeightDps)
            totalWeight += weight

            out.add(DisplayOption().add(point).multiply(weight))
        }

        return out.multiply(1.0f / totalWeight)
    }

    private fun getIconShapePath(context: Context): String {
        if (CONFIG_ICON_MASK_RES_ID == 0) {
            Timber.e("Icon mask res identifier failed to retrieve.")
            return ""
        }
        return context.resources.getString(CONFIG_ICON_MASK_RES_ID)
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

    private fun parseProfiles(
        parser: XmlResourceParser,
        tagName: String,
        block: () -> Unit
    ) {
        val depth = parser.depth
        var type = parser.next()
        while ((type != XmlPullParser.END_TAG || parser.depth > depth) &&
            type != XmlPullParser.END_DOCUMENT) {
            if (type == XmlPullParser.START_TAG && parser.name == tagName) {
                block()
            }
            type = parser.next()
        }
    }

    private fun dist(x0: Float, y0: Float, x1: Float, y1: Float): Float {
        return hypot((x1 - x0).toDouble(), (y1 - y0).toDouble()).toFloat()
    }

    private fun weight(x0: Float, y0: Float, x1: Float, y1: Float): Float {
        val d = dist(x0, y0, x1, y1)

        return if (d.compareTo(0f) == 0) Float.POSITIVE_INFINITY
        else (WEIGHT_EFFICIENT / d.toDouble().pow(WEIGHT_POWER)).toFloat()
    }

    private class GridOption(context: Context, attrs: AttributeSet) {
        val name: String
        val numColumns: Int

        init {
            context.obtainStyledAttributes(attrs, R.styleable.GridDisplayOption).apply {
                name = getString(R.styleable.GridDisplayOption_name) ?: ""
                numColumns = getInt(R.styleable.GridDisplayOption_numColumns, 0)
                recycle()
            }
        }

        companion object {
            const val TAG_NAME = "grid-option"
        }
    }

    private class DisplayOption {
        val gridOption: GridOption?
        private val name: String?
        val minWidthDps: Float
        val minHeightDps: Float
        val canBeDefault: Boolean

        var iconImageSize = 0f
        var iconTextSize = 0f
        var shortcutImageSize = 0f

        constructor() {
            gridOption = null
            name = null
            minWidthDps = 0f
            minHeightDps = 0f
            canBeDefault = false
        }

        constructor(context: Context, grid: GridOption, attrs: AttributeSet) {
            gridOption = grid
            context.obtainStyledAttributes(attrs, R.styleable.ProfileDisplayOption).apply {
                name = getString(R.styleable.ProfileDisplayOption_name)
                minWidthDps = getFloat(R.styleable.ProfileDisplayOption_minWidthDps, 0f)
                minHeightDps = getFloat(R.styleable.ProfileDisplayOption_minHeightDps, 0f)
                canBeDefault = getBoolean(
                    R.styleable.ProfileDisplayOption_canBeDefault,
                    false
                )

                iconImageSize = getFloat(R.styleable.ProfileDisplayOption_iconImageSize, 0f)
                iconTextSize = getFloat(R.styleable.ProfileDisplayOption_iconTextSize, 0f)
                shortcutImageSize = getFloat(
                    R.styleable.ProfileDisplayOption_shortcutImageSize,
                    0f
                )

                recycle()
            }
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

        companion object {
            const val TAG_NAME = "display-option"
        }
    }

    companion object {
        private const val NEAREST_NEIGHBOR = 3f
        private const val WEIGHT_EFFICIENT = 100000f
        private const val WEIGHT_POWER = 5.0
        private const val ICON_SIZE_DEFINED_IN_APP_DP = 48f

        private val CONFIG_ICON_MASK_RES_ID = Resources.getSystem().getIdentifier(
            "config_icon_mask",
            "string",
            "android"
        )
    }
}
