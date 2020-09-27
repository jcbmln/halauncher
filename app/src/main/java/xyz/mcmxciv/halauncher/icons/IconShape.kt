package xyz.mcmxciv.halauncher.icons

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.SparseArray
import android.util.TypedValue
import java.lang.Exception

abstract class IconShape {
    abstract val shapeType: ShapeType
    private var attrs: SparseArray<TypedValue>? = null

    abstract fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint)
    abstract fun addToPath(path: Path, offsetX: Float, offsetY: Float)

    fun getAttrValue(attr: Int): TypedValue? = attrs?.get(attr)

    abstract class PathShape : IconShape() {
        private val tempPath = Path()

        override fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint) {
            tempPath.reset()
            addToPath(tempPath, offsetX, offsetY)
            canvas.drawPath(tempPath, paint)
        }
    }

    class Circle(private val radius: Float) : IconShape() {
        override val shapeType: ShapeType = ShapeType.Circle

        override fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint) {
            canvas.drawCircle(radius + offsetX, radius + offsetY, radius, paint)
        }

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            path.addCircle(radius + offsetX, radius + offsetY, radius, Path.Direction.CW)
        }
    }

    class RoundedSquare(private val radius: Float) : IconShape() {
        override val shapeType: ShapeType = ShapeType.RoundedSquare

        override fun drawShape(canvas: Canvas, offsetX: Float, offsetY: Float, paint: Paint) {
            val cornerRadius = radius * RADIUS_RATIO
            canvas.drawRoundRect(
                offsetX,
                offsetY,
                radius + offsetX,
                radius + offsetY,
                cornerRadius,
                cornerRadius,
                paint
            )
        }

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            val cornerRadius = radius * RADIUS_RATIO
            path.addRoundRect(
                offsetX,
                offsetY,
                radius + offsetX,
                radius + offsetY,
                cornerRadius,
                cornerRadius,
                Path.Direction.CW
            )
        }
    }

    class TearDrop(private val radius: Float) : PathShape() {
        override val shapeType: ShapeType = ShapeType.TearDrop

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            path.addRoundRect(
                offsetX,
                offsetY,
                radius + offsetX,
                radius + offsetY,
                getRadii(radius, radius * RADIUS_RATIO),
                Path.Direction.CW
            )
        }

        private fun getRadii(r1: Float, r2: Float): FloatArray {
            val size = 8
            var index = 0
            val radii = FloatArray(size)

            while (index < size) {
                radii[index] = r1
                index++
            }

            radii[4] = r2
            radii[5] = r2

            return radii
        }
    }

    class Squircle(private val radius: Float) : PathShape() {
        override val shapeType: ShapeType = ShapeType.Squircle

        override fun addToPath(path: Path, offsetX: Float, offsetY: Float) {
            val x = radius + offsetX
            val y = radius + offsetX
            val control = radius - (radius * RADIUS_RATIO)

            path.moveTo(x, y - radius)
            addLeftCurve(x, y, radius, control, path)
            addRightCurve(x, y, radius, control, path)
            addLeftCurve(x, y, -radius, -control, path)
            addRightCurve(x, y, -radius, -control, path)
            path.close()
        }

        private fun addLeftCurve(x: Float, y: Float, r: Float, control: Float, path: Path) {
            path.cubicTo(
                x - control,
                y - r,
                x - r,
                y - control,
                x - r,
                y
            )
        }

        private fun addRightCurve(x: Float, y: Float, r: Float, control: Float, path: Path) {
            path.cubicTo(
                x - r,
                y + control,
                x - control,
                y + r,
                x,
                y + r
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
        private var path: Path? = null

        private const val RADIUS_RATIO = 0.15f

        fun getShapePath(radius: Float): Path {
            if (path == null) {
                val p = Path()
                val s = shape ?: Squircle(radius)
                s.addToPath(p, 0f, 0f)
                path = p
            }

            return path ?: createPath(radius)
        }

        private fun createPath(radius: Float): Path {
            val p = Path()
            val s = shape ?: Squircle(radius)
            s.addToPath(p, 0f, 0f)
            return p
        }

//        val shapePath: Path
//            get() {
//                if (path == null) {
//                    val p = Path()
//                    val s = shape ?: throw IllegalStateException("No shape is set.")
//                    s.addToPath(p, 0f, 0f)
//                    path = p
//                }
//
//                return path!!
//            }
    }
}
