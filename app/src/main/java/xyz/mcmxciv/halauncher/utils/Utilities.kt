/*
 * Copyright (C) 2008 The Android Open Source Project
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

package xyz.mcmxciv.halauncher.utils

//import com.android.launcher3.ItemInfoWithIcon.FLAG_ICON_BADGED

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ResolveInfo
import android.content.pm.ShortcutInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.os.DeadObjectException
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.os.TransactionTooLargeException
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.TtsSpan
import android.util.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Interpolator

//import com.android.launcher3.compat.LauncherAppsCompat
//import com.android.launcher3.compat.ShortcutConfigActivityInfo
//import com.android.launcher3.config.FeatureFlags
//import com.android.launcher3.dragndrop.FolderAdaptiveIcon
//import com.android.launcher3.graphics.RotationMode
//import com.android.launcher3.graphics.TintedDrawableSpan
//import com.android.launcher3.icons.LauncherIcons
//import com.android.launcher3.shortcuts.DeepShortcutManager
//import com.android.launcher3.shortcuts.ShortcutKey
//import com.android.launcher3.util.IntArray
//import com.android.launcher3.util.PackageManagerHelper
//import com.android.launcher3.views.Transposable
//import com.android.launcher3.widget.PendingAddShortcutInfo

import java.io.Closeable
import java.io.IOException
import java.lang.reflect.Method
import java.util.Arrays
import java.util.Locale
import java.util.StringTokenizer
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Various utilities shared amongst the Launcher's classes.
 */
object Utilities {
    private val TAG = "Launcher.Utilities"

    private val sTrimPattern =
        Pattern.compile("^[\\s|\\p{javaSpaceChar}]*(.*)[\\s|\\p{javaSpaceChar}]*$")

    private val sLoc0 = IntArray(2)
    private val sLoc1 = IntArray(2)
    private val sMatrix = Matrix()
    private val sInverseMatrix = Matrix()

    val ATLEAST_Q = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    val ATLEAST_P = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    val ATLEAST_OREO_MR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

    val ATLEAST_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    val SINGLE_FRAME_MS = 16

    /**
     * Set on a motion event dispatched from the nav bar. See [MotionEvent.setEdgeFlags].
     */
    val EDGE_NAV_BAR = 1 shl 8

    /**
     * Indicates if the device has a debug build. Should only be used to store additional info or
     * add extra logging and not for changing the app behavior.
     */
    val IS_DEBUG_DEVICE =
        Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT) == "eng"

    // An intent extra to indicate the horizontal scroll of the wallpaper.
    val EXTRA_WALLPAPER_OFFSET = "com.android.launcher3.WALLPAPER_OFFSET"
    val EXTRA_WALLPAPER_FLAVOR = "com.android.launcher3.WALLPAPER_FLAVOR"

    // These values are same as that in {@link AsyncTask}.
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = CPU_COUNT + 1
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
    private const val KEEP_ALIVE = 1

//    /**
//     * An [Executor] to be used with async task with no limit on the queue size.
//     */
//    val THREAD_POOL_EXECUTOR: Executor = ThreadPoolExecutor(
//        CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE.toLong(),
//        TimeUnit.SECONDS, LinkedBlockingQueue<Any>()
//    )

//    val isBootCompleted: Boolean
//        get() = "1" == getSystemProperty("sys.boot_completed", "1")
//
//    fun isDevelopersOptionsEnabled(context: Context): Boolean {
//        return Settings.Global.getInt(
//            context.applicationContext.contentResolver,
//            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
//        ) != 0
//    }
//
//    fun isPropertyEnabled(propertyName: String): Boolean {
//        return Log.isLoggable(propertyName, Log.VERBOSE)
//    }
//
//    fun existsStyleWallpapers(context: Context): Boolean {
//        val ri = context.packageManager.resolveActivity(
//            PackageManagerHelper.getStyleWallpapersIntent(context), 0
//        )
//        return ri != null
//    }

//    /**
//     * Given a coordinate relative to the descendant, find the coordinate in a parent view's
//     * coordinates.
//     *
//     * @param descendant The descendant to which the passed coordinate is relative.
//     * @param ancestor The root view to make the coordinates relative to.
//     * @param coord The coordinate that we want mapped.
//     * @param includeRootScroll Whether or not to account for the scroll of the descendant:
//     * sometimes this is relevant as in a child's coordinates within the descendant.
//     * @param ignoreTransform If true, view transform is ignored
//     * @param outRotation If not null, and {@param ignoreTransform} is true, this is set to the
//     * overall rotation of the view in degrees.
//     * @return The factor by which this descendant is scaled relative to this DragLayer. Caution
//     * this scale factor is assumed to be equal in X and Y, and so if at any point this
//     * assumption fails, we will need to return a pair of scale factors.
//     */
//    @JvmOverloads
//    fun getDescendantCoordRelativeToAncestor(
//        descendant: View, ancestor: View,
//        coord: FloatArray, includeRootScroll: Boolean, ignoreTransform: Boolean = false,
//        outRotation: FloatArray? = null
//    ): Float {
//        var scale = 1.0f
//        var v: View? = descendant
//        while (v !== ancestor && v != null) {
//            // For TextViews, scroll has a meaning which relates to the text position
//            // which is very strange... ignore the scroll.
//            if (v !== descendant || includeRootScroll) {
//                offsetPoints(coord, (-v.scrollX).toFloat(), (-v.scrollY).toFloat())
//            }
//
//            if (ignoreTransform) {
//                if (v is Transposable) {
//                    val m = (v as Transposable).getRotationMode()
//                    if (m.isTransposed) {
//                        sMatrix.setRotate(m.surfaceRotation, v.pivotX, v.pivotY)
//                        sMatrix.mapPoints(coord)
//
//                        if (outRotation != null) {
//                            outRotation[0] += m.surfaceRotation
//                        }
//                    }
//                }
//            } else {
//                v.matrix.mapPoints(coord)
//            }
//            offsetPoints(coord, v.left.toFloat(), v.top.toFloat())
//            scale *= v.scaleX
//
//            v = v.parent as View
//        }
//        return scale
//    }


//    /**
//     * Inverse of [.getDescendantCoordRelativeToAncestor].
//     */
//    fun mapCoordInSelfToDescendant(descendant: View, root: View, coord: FloatArray) {
//        sMatrix.reset()
//        var v = descendant
//        while (v !== root) {
//            sMatrix.postTranslate((-v.scrollX).toFloat(), (-v.scrollY).toFloat())
//            sMatrix.postConcat(v.matrix)
//            sMatrix.postTranslate(v.left.toFloat(), v.top.toFloat())
//            v = v.parent as View
//        }
//        sMatrix.postTranslate((-v.scrollX).toFloat(), (-v.scrollY).toFloat())
//        sMatrix.invert(sInverseMatrix)
//        sInverseMatrix.mapPoints(coord)
//    }

//    /**
//     * Sets {@param out} to be same as {@param in} by rounding individual values
//     */
//    fun roundArray(`in`: FloatArray, out: IntArray) {
//        for (i in `in`.indices) {
//            out[i] = Math.round(`in`[i])
//        }
//    }
//
//    fun offsetPoints(points: FloatArray, offsetX: Float, offsetY: Float) {
//        var i = 0
//        while (i < points.size) {
//            points[i] += offsetX
//            points[i + 1] += offsetY
//            i += 2
//        }
//    }

//    /**
//     * Utility method to determine whether the given point, in local coordinates,
//     * is inside the view, where the area of the view is expanded by the slop factor.
//     * This method is called while processing touch-move events to determine if the event
//     * is still within the view.
//     */
//    fun pointInView(v: View, localX: Float, localY: Float, slop: Float): Boolean {
//        return localX >= -slop && localY >= -slop && localX < v.width + slop &&
//                localY < v.height + slop
//    }

//    fun getCenterDeltaInScreenSpace(v0: View, v1: View): IntArray {
//        v0.getLocationInWindow(sLoc0)
//        v1.getLocationInWindow(sLoc1)
//
//        sLoc0[0] += (v0.measuredWidth * v0.scaleX / 2).toInt()
//        sLoc0[1] += (v0.measuredHeight * v0.scaleY / 2).toInt()
//        sLoc1[0] += (v1.measuredWidth * v1.scaleX / 2).toInt()
//        sLoc1[1] += (v1.measuredHeight * v1.scaleY / 2).toInt()
//        return intArrayOf(sLoc1[0] - sLoc0[0], sLoc1[1] - sLoc0[1])
//    }

//    fun scaleRectFAboutCenter(r: RectF, scale: Float) {
//        if (scale != 1.0f) {
//            val cx = r.centerX()
//            val cy = r.centerY()
//            r.offset(-cx, -cy)
//            r.left = r.left * scale
//            r.top = r.top * scale
//            r.right = r.right * scale
//            r.bottom = r.bottom * scale
//            r.offset(cx, cy)
//        }
//    }
//
//    fun scaleRectAboutCenter(r: Rect, scale: Float) {
//        if (scale != 1.0f) {
//            val cx = r.centerX()
//            val cy = r.centerY()
//            r.offset(-cx, -cy)
//            scaleRect(r, scale)
//            r.offset(cx, cy)
//        }
//    }
//
//    fun scaleRect(r: Rect, scale: Float) {
//        if (scale != 1.0f) {
//            r.left = (r.left * scale + 0.5f).toInt()
//            r.top = (r.top * scale + 0.5f).toInt()
//            r.right = (r.right * scale + 0.5f).toInt()
//            r.bottom = (r.bottom * scale + 0.5f).toInt()
//        }
//    }
//
//    fun insetRect(r: Rect, insets: Rect) {
//        r.left = Math.min(r.right, r.left + insets.left)
//        r.top = Math.min(r.bottom, r.top + insets.top)
//        r.right = Math.max(r.left, r.right - insets.right)
//        r.bottom = Math.max(r.top, r.bottom - insets.bottom)
//    }
//
//    fun shrinkRect(r: Rect, scaleX: Float, scaleY: Float): Float {
//        val scale = Math.min(Math.min(scaleX, scaleY), 1.0f)
//        if (scale < 1.0f) {
//            val deltaX = (r.width().toFloat() * (scaleX - scale) * 0.5f).toInt()
//            r.left += deltaX
//            r.right -= deltaX
//
//            val deltaY = (r.height().toFloat() * (scaleY - scale) * 0.5f).toInt()
//            r.top += deltaY
//            r.bottom -= deltaY
//        }
//        return scale
//    }
//
//    /**
//     * Maps t from one range to another range.
//     * @param t The value to map.
//     * @param fromMin The lower bound of the range that t is being mapped from.
//     * @param fromMax The upper bound of the range that t is being mapped from.
//     * @param toMin The lower bound of the range that t is being mapped to.
//     * @param toMax The upper bound of the range that t is being mapped to.
//     * @return The mapped value of t.
//     */
//    fun mapToRange(
//        t: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float,
//        interpolator: Interpolator
//    ): Float {
//        if (fromMin == fromMax || toMin == toMax) {
//            Log.e(TAG, "mapToRange: range has 0 length")
//            return toMin
//        }
//        val progress = getProgress(t, fromMin, fromMax)
//        return mapRange(interpolator.getInterpolation(progress), toMin, toMax)
//    }
//
//    fun getProgress(current: Float, min: Float, max: Float): Float {
//        return Math.abs(current - min) / Math.abs(max - min)
//    }
//
//    fun mapRange(value: Float, min: Float, max: Float): Float {
//        return min + value * (max - min)
//    }
//
//    fun isSystemApp(context: Context, intent: Intent): Boolean {
//        val pm = context.packageManager
//        val cn = intent.component
//        var packageName: String? = null
//        if (cn == null) {
//            val info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
//            if (info != null && info.activityInfo != null) {
//                packageName = info.activityInfo.packageName
//            }
//        } else {
//            packageName = cn.packageName
//        }
//        return if (packageName != null) {
//            try {
//                val info = pm.getPackageInfo(packageName, 0)
//                info != null && info.applicationInfo != null &&
//                        info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
//            } catch (e: NameNotFoundException) {
//                false
//            }
//
//        } else {
//            false
//        }
//    }
//
//    /*
//     * Finds a system apk which had a broadcast receiver listening to a particular action.
//     * @param action intent action used to find the apk
//     * @return a pair of apk package name and the resources.
//     */
//    internal fun findSystemApk(action: String, pm: PackageManager): Pair<String, Resources>? {
//        val intent = Intent(action)
//        for (info in pm.queryBroadcastReceivers(intent, 0)) {
//            if (info.activityInfo != null && info.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
//                val packageName = info.activityInfo.packageName
//                try {
//                    val res = pm.getResourcesForApplication(packageName)
//                    return Pair.create(packageName, res)
//                } catch (e: NameNotFoundException) {
//                    Log.w(TAG, "Failed to find resources for $packageName")
//                }
//
//            }
//        }
//        return null
//    }
//
//    /**
//     * Trims the string, removing all whitespace at the beginning and end of the string.
//     * Non-breaking whitespaces are also removed.
//     */
//    fun trim(s: CharSequence?): String? {
//        if (s == null) {
//            return null
//        }
//
//        // Just strip any sequence of whitespace or java space characters from the beginning and end
//        val m = sTrimPattern.matcher(s)
//        return m.replaceAll("$1")
//    }

    /**
     * Calculates the height of a given string at a specific text size.
     */
    fun calculateTextHeight(textSizePx: Float): Int {
        val p = Paint()
        p.textSize = textSizePx
        val fm = p.fontMetrics
        return ceil((fm.bottom - fm.top).toDouble()).toInt()
    }
//
//    /**
//     * Convenience println with multiple args.
//     */
//    fun println(key: String, vararg args: Any) {
//        val b = StringBuilder()
//        b.append(key)
//        b.append(": ")
//        var isFirstArgument = true
//        for (arg in args) {
//            if (isFirstArgument) {
//                isFirstArgument = false
//            } else {
//                b.append(", ")
//            }
//            b.append(arg)
//        }
//        println(b.toString())
//    }
//
//    fun isRtl(res: Resources): Boolean {
//        return res.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
//    }
//
//    /**
//     * Returns true if the intent is a valid launch intent for a launcher activity of an app.
//     * This is used to identify shortcuts which are different from the ones exposed by the
//     * applications' manifest file.
//     *
//     * @param launchIntent The intent that will be launched when the shortcut is clicked.
//     */
//    fun isLauncherAppTarget(launchIntent: Intent?): Boolean {
//        if (launchIntent != null
//            && Intent.ACTION_MAIN == launchIntent.action
//            && launchIntent.component != null
//            && launchIntent.categories != null
//            && launchIntent.categories.size == 1
//            && launchIntent.hasCategory(Intent.CATEGORY_LAUNCHER)
//            && TextUtils.isEmpty(launchIntent.dataString)
//        ) {
//            // An app target can either have no extra or have ItemInfo.EXTRA_PROFILE.
//            val extras = launchIntent.extras
//            return extras == null || extras.keySet().isEmpty()
//        }
//        return false
//    }
//
    fun dpiFromPx(size: Int, metrics: DisplayMetrics): Float {
        val densityRatio = metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
        return size / densityRatio
    }

    fun pxFromSp(size: Float, metrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            size, metrics
        ).roundToInt()
    }

    fun pxFromDp(size: Float, metrics: DisplayMetrics): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, metrics).roundToInt()
    }
//
//    fun createDbSelectionQuery(columnName: String, values: IntArray): String {
//        return String.format(Locale.ENGLISH, "%s IN (%s)", columnName, values.toConcatString())
//    }
//
//    fun getSystemProperty(property: String, defaultValue: String): String {
//        try {
//            val clazz = Class.forName("android.os.SystemProperties")
//            val getter = clazz.getDeclaredMethod("get", String::class.java)
//            val value = getter.invoke(null, property) as String
//            if (!TextUtils.isEmpty(value)) {
//                return value
//            }
//        } catch (e: Exception) {
//            Log.d(TAG, "Unable to read system properties")
//        }
//
//        return defaultValue
//    }
//
//    /**
//     * Ensures that a value is within given bounds. Specifically:
//     * If value is less than lowerBound, return lowerBound; else if value is greater than upperBound,
//     * return upperBound; else return value unchanged.
//     */
//    fun boundToRange(value: Int, lowerBound: Int, upperBound: Int): Int {
//        return Math.max(lowerBound, Math.min(value, upperBound))
//    }
//
//    /**
//     * @see .boundToRange
//     */
//    fun boundToRange(value: Float, lowerBound: Float, upperBound: Float): Float {
//        return Math.max(lowerBound, Math.min(value, upperBound))
//    }
//
//    /**
//     * @see .boundToRange
//     */
//    fun boundToRange(value: Long, lowerBound: Long, upperBound: Long): Long {
//        return Math.max(lowerBound, Math.min(value, upperBound))
//    }
//
//    /**
//     * Wraps a message with a TTS span, so that a different message is spoken than
//     * what is getting displayed.
//     * @param msg original message
//     * @param ttsMsg message to be spoken
//     */
//    fun wrapForTts(msg: CharSequence, ttsMsg: String): CharSequence {
//        val spanned = SpannableString(msg)
//        spanned.setSpan(
//            TtsSpan.TextBuilder(ttsMsg).build(),
//            0, spanned.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
//        return spanned
//    }
//
//    /**
//     * Prefixes a text with the provided icon
//     */
//    fun prefixTextWithIcon(context: Context, iconRes: Int, msg: CharSequence): CharSequence {
//        // Update the hint to contain the icon.
//        // Prefix the original hint with two spaces. The first space gets replaced by the icon
//        // using span. The second space is used for a singe space character between the hint
//        // and the icon.
//        val spanned = SpannableString("  $msg")
//        spanned.setSpan(
//            TintedDrawableSpan(context, iconRes),
//            0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE
//        )
//        return spanned
//    }

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            AppFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE
        )
    }

    fun getDevicePrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            AppFiles.DEVICE_PREFERENCES_KEY, Context.MODE_PRIVATE
        )
    }

//    fun areAnimationsEnabled(context: Context): Boolean {
//        return if (ATLEAST_OREO)
//            ValueAnimator.areAnimatorsEnabled()
//        else
//            !context.getSystemService(PowerManager::class.java)!!.isPowerSaveMode
//    }
//
//    fun isWallpaperAllowed(context: Context): Boolean {
//        return context.getSystemService(WallpaperManager::class.java)!!.isSetWallpaperAllowed
//    }
//
//    fun closeSilently(c: Closeable?) {
//        if (c != null) {
//            try {
//                c.close()
//            } catch (e: IOException) {
//                if (FeatureFlags.IS_DOGFOOD_BUILD) {
//                    Log.d(TAG, "Error closing", e)
//                }
//            }
//
//        }
//    }
//
//    fun isBinderSizeError(e: Exception): Boolean {
//        return e.cause is TransactionTooLargeException || e.cause is DeadObjectException
//    }
//
//    /**
//     * Utility method to post a runnable on the handler, skipping the synchronization barriers.
//     */
//    fun postAsyncCallback(handler: Handler, callback: Runnable) {
//        val msg = Message.obtain(handler, callback)
//        msg.isAsynchronous = true
//        handler.sendMessage(msg)
//    }
//
//    /**
//     * Parses a string encoded using [.getPointString]
//     */
//    fun parsePoint(point: String): Point {
//        val split = point.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        return Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]))
//    }
//
//    /**
//     * Encodes a point to string to that it can be persisted atomically.
//     */
//    fun getPointString(x: Int, y: Int): String {
//        return String.format(Locale.ENGLISH, "%d,%d", x, y)
//    }
//
//    fun unregisterReceiverSafely(context: Context, receiver: BroadcastReceiver) {
//        try {
//            context.unregisterReceiver(receiver)
//        } catch (e: IllegalArgumentException) {
//        }
//
//    }
//
//    /**
//     * Returns the full drawable for {@param info}.
//     * @param outObj this is set to the internal data associated with {@param info},
//     * eg [LauncherActivityInfo] or [ShortcutInfo].
//     */
//    fun getFullDrawable(
//        launcher: Launcher, info: ItemInfo, width: Int, height: Int,
//        flattenDrawable: Boolean, outObj: Array<Any>
//    ): Drawable? {
//        val appState = LauncherAppState.getInstance(launcher)
//        if (info.itemType === LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
//            val activityInfo = LauncherAppsCompat.getInstance(launcher)
//                .resolveActivity(info.getIntent(), info.user)
//            outObj[0] = activityInfo
//            return if (activityInfo != null)
//                appState.getIconCache()
//                    .getFullResIcon(activityInfo, flattenDrawable)
//            else
//                null
//        } else if (info.itemType === LauncherSettings.Favorites.ITEM_TYPE_DEEP_SHORTCUT) {
//            if (info is PendingAddShortcutInfo) {
//                val activityInfo = (info as PendingAddShortcutInfo).activityInfo
//                outObj[0] = activityInfo
//                return activityInfo.getFullResIcon(appState.getIconCache())
//            }
//            val key = ShortcutKey.fromItemInfo(info)
//            val sm = DeepShortcutManager.getInstance(launcher)
//            val si = sm.queryForFullDetails(
//                key.componentName.getPackageName(), Arrays.asList(key.getId()), key.user
//            )
//            if (si.isEmpty()) {
//                return null
//            } else {
//                outObj[0] = si.get(0)
//                return sm.getShortcutIconDrawable(
//                    si.get(0),
//                    appState.getInvariantDeviceProfile().fillResIconDpi
//                )
//            }
//        } else if (info.itemType === LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
//            val icon = FolderAdaptiveIcon.createFolderAdaptiveIcon(
//                launcher, info.id, Point(width, height)
//            )
//                ?: return null
//            outObj[0] = icon
//            return icon
//        } else {
//            return null
//        }
//    }
//
//    /**
//     * For apps icons and shortcut icons that have badges, this method creates a drawable that can
//     * later on be rendered on top of the layers for the badges. For app icons, work profile badges
//     * can only be applied. For deep shortcuts, when dragged from the pop up container, there's no
//     * badge. When dragged from workspace or folder, it may contain app AND/OR work profile badge
//     */
//    @TargetApi(Build.VERSION_CODES.O)
//    fun getBadge(launcher: Launcher, info: ItemInfo, obj: Any): Drawable {
//        val appState = LauncherAppState.getInstance(launcher)
//        val iconSize = appState.getInvariantDeviceProfile().iconBitmapSize
//        if (info.itemType === LauncherSettings.Favorites.ITEM_TYPE_DEEP_SHORTCUT) {
//            val iconBadged =
//                info is ItemInfoWithIcon && (info as ItemInfoWithIcon).runtimeStatusFlags and FLAG_ICON_BADGED > 0
//            if (info.id === ItemInfo.NO_ID && !iconBadged || obj !is ShortcutInfo) {
//                // The item is not yet added on home screen.
//                return FixedSizeEmptyDrawable(iconSize)
//            }
//            val li = LauncherIcons.obtain(appState.getContext())
//            val badge = li.getShortcutInfoBadge(obj, appState.getIconCache()).iconBitmap
//            li.recycle()
//            val badgeSize = launcher.getResources().getDimension(R.dimen.profile_badge_size)
//            val insetFraction = (iconSize - badgeSize) / iconSize
//            return InsetDrawable(
//                FastBitmapDrawable(badge),
//                insetFraction, insetFraction, 0, 0
//            )
//        } else return if (info.itemType === LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
//            (obj as FolderAdaptiveIcon).getBadge()
//        } else {
//            launcher.getPackageManager()
//                .getUserBadgedIcon(FixedSizeEmptyDrawable(iconSize), info.user)
//        }
//    }
//
//    fun getIntArrayFromString(tokenized: String): IntArray {
//        val tokenizer = StringTokenizer(tokenized, ",")
//        val array = IntArray(tokenizer.countTokens())
//        var count = 0
//        while (tokenizer.hasMoreTokens()) {
//            array[count] = Integer.parseInt(tokenizer.nextToken())
//            count++
//        }
//        return array
//    }
//
//    fun getStringFromIntArray(array: IntArray): String {
//        val str = StringBuilder()
//        for (value in array) {
//            str.append(value).append(",")
//        }
//        return str.toString()
//    }
//
//    fun squaredHypot(x: Float, y: Float): Float {
//        return x * x + y * y
//    }
//
//    fun squaredTouchSlop(context: Context): Float {
//        val slop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
//        return slop * slop
//    }
//
//    private class FixedSizeEmptyDrawable(private val mSize: Int) :
//        ColorDrawable(Color.TRANSPARENT) {
//
//        override fun getIntrinsicHeight(): Int {
//            return mSize
//        }
//
//        override fun getIntrinsicWidth(): Int {
//            return mSize
//        }
//    }

    /**
     * Creates a map for attribute-name to value for all the values in {@param attrs} which can be
     * held in memory for later use.
     */
    fun createValueMap(
        context: Context, attrSet: AttributeSet,
        keysToIgnore: IntArray
    ): SparseArray<TypedValue> {
        val count = attrSet.attributeCount
        var attrNameArray = IntArray(count)
        for (i in 0 until count) {
            attrNameArray = attrNameArray.plus(attrSet.getAttributeNameResource(i))
        }
        attrNameArray = attrNameArray.filter { item ->
            keysToIgnore.contains(item)
        }.toIntArray()

        val result = SparseArray<TypedValue>(attrNameArray.size)
        val ta = context.obtainStyledAttributes(attrSet, attrNameArray)

        for (i in attrNameArray.indices) {
            val tv = TypedValue()
            ta.getValue(i, tv)
            result.put(attrNameArray[i], tv)
        }

        ta.recycle()

        return result
    }
}
