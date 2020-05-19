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

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.SparseArray
import android.util.TypedValue

/**
 * Abstract representation of the shape of an icon shape
 */
abstract class IconShape {
    abstract val shapeType: ShapeType
    private var attrs: SparseArray<TypedValue>? = null

    abstract fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint)

    abstract fun addToPath(path: Path, offsetX: Float, offsetY: Float)

    fun getAttrValue(attr: Int): TypedValue? {
        return if (attrs == null) null else attrs!!.get(attr)
    }

    /**
     * Abstract shape which draws using [Path]
     */
    abstract class PathShape : IconShape() {
        private val tempPath = Path()

        override fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint) {
            tempPath.reset()
            addToPath(tempPath, offsetX, offsetY)
            canvas.drawPath(tempPath, paint)
        }
    }

    private class Circle(private val radius: Float) : IconShape() {
        override val shapeType: ShapeType = ShapeType.Circle

        override fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint) {
            canvas.drawCircle(
                this.radius + offsetX, this.radius + offsetY,
                this.radius, paint)
        }

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            path.addCircle(radius + offsetX, radius + offsetY, radius, Path.Direction.CW)
        }
    }

    private class RoundedSquare(
        /**
         * Ratio of corner radius to half size.
         */
        private val radius: Float
    ) : IconShape() {
        override val shapeType: ShapeType = ShapeType.RoundedSquare

        override fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint) {
            val cx = radius + offsetX
            val cy = radius + offsetY
            val cr = radius * RADIUS_RATIO
            canvas.drawRoundRect(cx - radius, cy - radius, cx + radius, cy + radius, cr, cr, paint)
        }

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            val cx = radius + offsetX
            val cy = radius + offsetY
            val cr = radius * RADIUS_RATIO
            path.addRoundRect(
                cx - radius, cy - radius, cx + radius, cy + radius, cr, cr,
                Path.Direction.CW
            )
        }
    }

    class TearDrop(private val radius: Float) : PathShape() {
        override val shapeType: ShapeType = ShapeType.TearDrop
        private val mTempRadii = FloatArray(8)

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            val r2 = radius * RADIUS_RATIO
            val cx = radius + offsetX
            val cy = radius + offsetY

            path.addRoundRect(
                cx - radius, cy - radius, cx + radius, cy + radius,
                getRadiiArray(radius, r2), Path.Direction.CW
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
    }

    class Squircle(private val radius: Float) : PathShape() {
        override val shapeType: ShapeType = ShapeType.Squircle

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            val cx = radius + offsetX
            val cy = radius + offsetY
            val control = radius - radius * RADIUS_RATIO

            path.moveTo(cx, cy - radius)
            addLeftCurve(cx, cy, radius, control, path)
            addRightCurve(cx, cy, radius, control, path)
            addLeftCurve(cx, cy, -radius, -control, path)
            addRightCurve(cx, cy, -radius, -control, path)
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
    }

    enum class ShapeType {
        Circle,
        RoundedSquare,
        TearDrop,
        Squircle;

        companion object {
            fun toShapeType(enum: String): ShapeType {
                return try {
                    valueOf(enum)
                } catch (ex: Exception) {
                    Squircle
                }
            }
        }
    }

    companion object {
        private var shape: IconShape? = null
        private var sShapePath: Path? = null

        private const val RADIUS_RATIO = 0.15f

        val shapePath: Path
            get() {
                if (sShapePath == null) {
                    val p = Path()
                    shape?.addToPath(p, 0f, 0f)
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
    }
}
