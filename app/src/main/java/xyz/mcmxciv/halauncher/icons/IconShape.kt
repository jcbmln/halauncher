/*
 * Copyright (C) 2018 The Android Open Source Project
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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.FloatArrayEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider

//import com.android.launcher3.R
//import com.android.launcher3.Utilities
//import com.android.launcher3.anim.RoundedRectRevealOutlineProvider
//import com.android.launcher3.icons.GraphicsUtils
//import com.android.launcher3.icons.IconNormalizer
//import com.android.launcher3.util.IntArray
//import com.android.launcher3.util.Themes
//import com.android.launcher3.views.ClipPathView

import xyz.mcmxciv.halauncher.anim.RoundedRectRevealOutlineProvider
import xyz.mcmxciv.halauncher.icons.IconNormalizer.Companion.ICON_VISIBLE_AREA_FACTOR
import xyz.mcmxciv.halauncher.views.ClipPathView

/**
 * Abstract representation of the shape of an icon shape
 */
abstract class IconShape {
    abstract val shapeType: ShapeType
    private var attrs: SparseArray<TypedValue>? = null

    open fun enableShapeDetection(): Boolean {
        return false
    }

    abstract fun drawShape(
        canvas: Canvas, offsetX: Float, offsetY: Float, radius: Float,
        paint: Paint
    )

    abstract fun addToPath(path: Path, offsetX: Float, offsetY: Float, radius: Float)

    abstract fun <T> createRevealAnimator(
        target: T,
        startRect: Rect, endRect: Rect, endRadius: Float, isReversed: Boolean
    ): Animator where T : View, T : ClipPathView

    fun getAttrValue(attr: Int): TypedValue? {
        return if (attrs == null) null else attrs!!.get(attr)
    }

    /**
     * Abstract shape where the reveal animation is a derivative of a round rect animation
     */
    private abstract class SimpleRectShape : IconShape() {
        override fun <T> createRevealAnimator(
            target: T,
            startRect: Rect, endRect: Rect, endRadius: Float, isReversed: Boolean
        ): Animator where T : View, T : ClipPathView {
            return object : RoundedRectRevealOutlineProvider(
                getStartRadius(startRect), endRadius, startRect, endRect
            ) {
                override fun shouldRemoveElevationDuringAnimation(): Boolean {
                    return true
                }
            }.createRevealAnimator(target, isReversed)
        }

        protected abstract fun getStartRadius(startRect: Rect): Float
    }

    /**
     * Abstract shape which draws using [Path]
     */
    abstract class PathShape : IconShape() {

        private val mTmpPath = Path()

        override fun drawShape(
            canvas: Canvas, offsetX: Float, offsetY: Float, radius: Float,
            paint: Paint
        ) {
            mTmpPath.reset()
            addToPath(mTmpPath, offsetX, offsetY, radius)
            canvas.drawPath(mTmpPath, paint)
        }

        protected abstract fun newUpdateListener(
            startRect: Rect, endRect: Rect, endRadius: Float, outPath: Path
        ): AnimatorUpdateListener

        override fun <T> createRevealAnimator(
            target: T,
            startRect: Rect, endRect: Rect, endRadius: Float, isReversed: Boolean
        ): Animator where T : View, T : ClipPathView {
            val path = Path()
            val listener = newUpdateListener(startRect, endRect, endRadius, path)

            val va =
                if (isReversed) ValueAnimator.ofFloat(1f, 0f) else ValueAnimator.ofFloat(0f, 1f)
            va.addListener(object : AnimatorListenerAdapter() {
                private var mOldOutlineProvider: ViewOutlineProvider? = null

                override fun onAnimationStart(animation: Animator) {
                    mOldOutlineProvider = target.outlineProvider
                    target.outlineProvider = null

                    target.translationZ = -target.elevation
                }

                override fun onAnimationEnd(animation: Animator) {
                    target.translationZ = 0f
                    target.setClipPath(null)
                    target.outlineProvider = mOldOutlineProvider
                }
            })

            va.addUpdateListener { anim ->
                path.reset()
                listener.onAnimationUpdate(anim)
                target.setClipPath(path)
            }

            return va
        }
    }

    private class Circle(private val radius1: Float) : SimpleRectShape() {
        override val shapeType: ShapeType = ShapeType.Circle

        override fun drawShape(
            canvas: Canvas,
            offsetX: Float,
            offsetY: Float,
            radius: Float,
            paint: Paint
        ) {
            canvas.drawCircle(radius1 + offsetX, radius1 + offsetY, radius1, paint)
        }

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float, radius: Float) {
            path.addCircle(radius1 + offsetX, radius1 + offsetY, radius1, Path.Direction.CW)
        }

        override fun getStartRadius(startRect: Rect): Float {
            return startRect.width() / 2f
        }

        override fun enableShapeDetection(): Boolean {
            return true
        }
    }

    private class RoundedSquare(
        /**
         * Ratio of corner radius to half size.
         */
        private val mRadiusRatio: Float
    ) : SimpleRectShape() {
        override val shapeType: ShapeType = ShapeType.RoundedSquare

        override fun drawShape(
            canvas: Canvas,
            offsetX: Float,
            offsetY: Float,
            radius: Float,
            paint: Paint
        ) {
            val cx = radius + offsetX
            val cy = radius + offsetY
            val cr = radius * mRadiusRatio
            canvas.drawRoundRect(cx - radius, cy - radius, cx + radius, cy + radius, cr, cr, paint)
        }

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float, radius: Float) {
            val cx = radius + offsetX
            val cy = radius + offsetY
            val cr = radius * mRadiusRatio
            path.addRoundRect(
                cx - radius, cy - radius, cx + radius, cy + radius, cr, cr,
                Path.Direction.CW
            )
        }

        override fun getStartRadius(startRect: Rect): Float {
            return startRect.width() / 2f * mRadiusRatio
        }
    }

    class TearDrop(
        /**
         * Radio of short radius to large radius, based on the shape options defined in the config.
         */
        private val mRadiusRatio: Float
    ) : PathShape() {
        override val shapeType: ShapeType = ShapeType.TearDrop
        private val mTempRadii = FloatArray(8)

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float, radius: Float) {
            val r2 = radius * mRadiusRatio
            val cx = radius + offsetX
            val cy = radius + offsetY

            path.addRoundRect(
                cx - radius, cy - radius, cx + radius, cy + radius, getRadiiArray(radius, r2),
                Path.Direction.CW
            )
        }

        private fun getRadiiArray(r1: Float, r2: Float): FloatArray {
            mTempRadii[7] = r1
            mTempRadii[6] = mTempRadii[7]
            mTempRadii[3] = mTempRadii[6]
            mTempRadii[2] = mTempRadii[3]
            mTempRadii[1] = mTempRadii[2]
            mTempRadii[0] = mTempRadii[1]
            mTempRadii[5] = r2
            mTempRadii[4] = mTempRadii[5]
            return mTempRadii
        }

        override fun newUpdateListener(
            startRect: Rect, endRect: Rect,
            endRadius: Float, outPath: Path
        ): AnimatorUpdateListener {
            val r1 = startRect.width() / 2f
            val r2 = r1 * mRadiusRatio

            val startValues = floatArrayOf(
                startRect.left.toFloat(),
                startRect.top.toFloat(),
                startRect.right.toFloat(),
                startRect.bottom.toFloat(),
                r1,
                r2
            )
            val endValues = floatArrayOf(
                endRect.left.toFloat(),
                endRect.top.toFloat(),
                endRect.right.toFloat(),
                endRect.bottom.toFloat(),
                endRadius,
                endRadius
            )

            val evaluator = FloatArrayEvaluator(FloatArray(6))

            return { anim: ValueAnimator ->
                val progress = anim.animatedValue as Float
                val values = evaluator.evaluate(progress, startValues, endValues)
                outPath.addRoundRect(
                    values[0], values[1], values[2], values[3],
                    getRadiiArray(values[4], values[5]), Path.Direction.CW
                )
            } as AnimatorUpdateListener
        }
    }

    class Squircle(
        /**
         * Radio of radius to circle radius, based on the shape options defined in the config.
         */
        private val radius1: Float
    ) : PathShape() {
        override val shapeType: ShapeType = ShapeType.Squircle

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float, radius: Float) {
            val cx = radius1 + offsetX
            val cy = radius1 + offsetY
            val control = radius1 - radius1 * RADIUS_RATIO

            path.moveTo(cx, cy - radius1)
            addLeftCurve(cx, cy, radius1, control, path)
            addRightCurve(cx, cy, radius1, control, path)
            addLeftCurve(cx, cy, -radius1, -control, path)
            addRightCurve(cx, cy, -radius1, -control, path)
            path.close()
        }

        private fun addLeftCurve(cx: Float, cy: Float, r: Float, control: Float, path: Path) {
            path.cubicTo(
                cx - control, cy - r,
                cx - r, cy - control,
                cx - r, cy
            )
        }

        private fun addRightCurve(cx: Float, cy: Float, r: Float, control: Float, path: Path) {
            path.cubicTo(
                cx - r, cy + control,
                cx - control, cy + r,
                cx, cy + r
            )
        }

        override fun newUpdateListener(
            startRect: Rect, endRect: Rect,
            endRadius: Float, outPath: Path
        ): AnimatorUpdateListener {

            val startCX = startRect.exactCenterX()
            val startCY = startRect.exactCenterY()
            val startR = startRect.width() / 2f
            val startControl = startR - startR * RADIUS_RATIO
            val startHShift = 0f
            val startVShift = 0f

            val endCX = endRect.exactCenterX()
            val endCY = endRect.exactCenterY()
            // Approximate corner circle using bezier curves
            // http://spencermortensen.com/articles/bezier-circle/
            val endControl = endRadius * 0.551915024494f
            val endHShift = endRect.width() / 2f - endRadius
            val endVShift = endRect.height() / 2f - endRadius

            return { anim: ValueAnimator ->
                val progress = anim.animatedValue as Float

                val cx = (1 - progress) * startCX + progress * endCX
                val cy = (1 - progress) * startCY + progress * endCY
                val r = (1 - progress) * startR + progress * endRadius
                val control = (1 - progress) * startControl + progress * endControl
                val hShift = (1 - progress) * startHShift + progress * endHShift
                val vShift = (1 - progress) * startVShift + progress * endVShift

                outPath.moveTo(cx, cy - vShift - r)
                outPath.rLineTo(-hShift, 0f)

                addLeftCurve(cx - hShift, cy - vShift, r, control, outPath)
                outPath.rLineTo(0f, vShift + vShift)

                addRightCurve(cx - hShift, cy + vShift, r, control, outPath)
                outPath.rLineTo(hShift + hShift, 0f)

                addLeftCurve(cx + hShift, cy + vShift, -r, -control, outPath)
                outPath.rLineTo(0f, -vShift - vShift)

                addRightCurve(cx + hShift, cy - vShift, -r, -control, outPath)
                outPath.close()
            } as AnimatorUpdateListener
        }
    }

    enum class ShapeType {
        Circle,
        RoundedSquare,
        TearDrop,
        Squircle
    }

    companion object {
        private var shape: IconShape? = null
        private var sShapePath: Path? = null
        var normalizationScale = ICON_VISIBLE_AREA_FACTOR
            private set

        private const val DEFAULT_PATH_SIZE = 100
        private const val RADIUS_RATIO = 0.15f

        val shapePath: Path
            get() {
                if (sShapePath == null) {
                    val p = Path()
                    shape?.addToPath(p, 0f, 0f, DEFAULT_PATH_SIZE * 0.5f)
                    sShapePath = p
                }
                return sShapePath as Path
            }

        fun setShape(type: ShapeType, radius: Float) {
            shape = when (type) {
                ShapeType.Circle -> Circle(radius)
                ShapeType.RoundedSquare -> RoundedSquare(radius)
                ShapeType.TearDrop -> TearDrop(radius)
                ShapeType.Squircle -> Squircle(radius)
            }
        }

//        /**
//         * Initializes the shape which is closest to the [AdaptiveIconDrawable]
//         */
//        fun init(context: Context) {
//            if (!Utilities.ATLEAST_OREO) {
//                return
//            }
//            pickBestShape(context)
//        }
//
//        private fun getShapeDefinition(type: ShapeType, radius: Float): IconShape {
//            return when (type) {
//                ShapeType.Circle -> Circle(radius)
//                ShapeType.RoundedSquare -> RoundedSquare(radius)
//                ShapeType.TearDrop -> TearDrop(radius)
//                ShapeType.Squircle -> Squircle(radius)
//            }
        }

//        private fun getAllShapes(context: Context): List<IconShape> {
//            val result = ArrayList<IconShape>()
//            try {
//                context.resources.getXml(R.xml.folder_shapes).use { parser ->
//
//                    // Find the root tag
//                    var type: Int = parser.next()
//                    @Suppress("ControlFlowWithEmptyBody")
//                    while (type != XmlPullParser.END_TAG
//                        && type != XmlPullParser.END_DOCUMENT
//                        && "shapes" != parser.name
//                    );
//
//                    val depth = parser.depth
//                    val radiusAttr = intArrayOf(R.attr.folderIconRadius)
//                    val keysToIgnore = IntArray(0)
//
//                    type = parser.next()
//                    while ((parser.next() != XmlPullParser.END_TAG || parser.depth > depth) &&
//                        type != XmlPullParser.END_DOCUMENT
//                    ) {
//
//                        if (type == XmlPullParser.START_TAG) {
//                            val attrs = Xml.asAttributeSet(parser)
//                            val a = context.obtainStyledAttributes(attrs, radiusAttr)
//                            val shape = getShapeDefinition(parser.name, a.getFloat(0, 1f))
//                            a.recycle()
//
//                            shape.attrs = Utilities.createValueMap(context, attrs, keysToIgnore)
//                            result.add(shape)
//                        }
//                    }
//                }
//            } catch (e: IOException) {
//                throw RuntimeException(e)
//            } catch (e: XmlPullParserException) {
//                throw RuntimeException(e)
//            }
//
//            return result
//        }

//        @TargetApi(Build.VERSION_CODES.O)
//        protected fun pickBestShape(context: Context) {
//            // Pick any large size
//            val size = 200
//
//            val full = Region(0, 0, size, size)
//            val iconR = Region()
//            val drawable = AdaptiveIconDrawable(
//                ColorDrawable(Color.BLACK), ColorDrawable(Color.BLACK)
//            )
//            drawable.setBounds(0, 0, size, size)
//            iconR.setPath(drawable.iconMask, full)
//
//            val shapePath = Path()
//            val shapeR = Region()
//
//            // Find the shape with minimum area of divergent region.
//            var minArea = Integer.MAX_VALUE
//            var closestShape: IconShape? = null
//            for (shape in getAllShapes(context)) {
//                shapePath.reset()
//                shape.addToPath(shapePath, 0f, 0f, size / 2f)
//                shapeR.setPath(shapePath, full)
//                shapeR.op(iconR, Op.XOR)
//
//                val area = GraphicsUtils.getArea(shapeR)
//                if (area < minArea) {
//                    minArea = area
//                    closestShape = shape
//                }
//            }
//
//            if (closestShape != null) {
//                shape = closestShape
//            }
//
//            // Initialize shape properties
//            drawable.setBounds(0, 0, DEFAULT_PATH_SIZE, DEFAULT_PATH_SIZE)
//            sShapePath = Path(drawable.iconMask)
//            normalizationScale = IconNormalizer.normalizeAdaptiveIcon(drawable, size, null)
//        }
}
