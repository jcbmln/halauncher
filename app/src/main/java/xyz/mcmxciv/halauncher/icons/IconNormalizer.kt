package xyz.mcmxciv.halauncher.icons

import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import xyz.mcmxciv.halauncher.device.DeviceProfile
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class IconNormalizer @Inject constructor(
    resourceProvider: ResourceProvider,
    deviceProfile: DeviceProfile
) {
    private val maxSize = deviceProfile.iconBitmapSize * 2
    private val bitmap = Bitmap.createBitmap(maxSize, maxSize, Bitmap.Config.ALPHA_8)
    private val canvas = Canvas(bitmap)
    private val pixels = ByteArray(maxSize * maxSize)
    private val leftBorder = FloatArray(maxSize)
    private val rightBorder = FloatArray(maxSize)
    private val bounds = Rect()
    private val adaptiveIconBounds = RectF()
    private val paintMaskShape = Paint()
    private val paintMaskShapeOutline = Paint()
    private var adaptiveIconScale = SCALE_NOT_INITIALIZED
    private val matrix = Matrix()
    private val shapePath = Path()

    init {
        paintMaskShape.apply {
            color = Color.RED
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        }
        paintMaskShapeOutline.apply {
            strokeWidth = 2 * resourceProvider.displayMetrics.density
            style = Paint.Style.STROKE
            color = Color.BLACK
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    @Synchronized
    fun getScale(d: Drawable, outBounds: RectF?, path: Path?, outMaskShape: BooleanArray?): Float {
        if (d is AdaptiveIconDrawable) {
            if (adaptiveIconScale != SCALE_NOT_INITIALIZED) {
                outBounds?.set(adaptiveIconBounds)
                return adaptiveIconScale
            }
        }

        var width = d.intrinsicWidth
        var height = d.intrinsicHeight

        if (width <= 0 || height <= 0) {
            width = if (width <= 0 || width > maxSize) maxSize else width
            height = if (height <= 0 || height > maxSize) maxSize else height
        } else if (width > maxSize || height > maxSize) {
            val max = max(width, height)
            width = maxSize * width / max
            height = maxSize * height / max
        }

        bitmap.eraseColor(Color.TRANSPARENT)
        d.setBounds(0, 0, width, height)
        d.draw(canvas)

        val buffer = ByteBuffer.wrap(pixels)
        buffer.rewind()
        bitmap.copyPixelsToBuffer(buffer)

        var topY = -1
        var bottomY = -1
        var leftX = maxSize + 1
        var rightX = -1

        var index = 0
        val rowSizeDiff = maxSize - width
        var firstX: Int
        var lastX: Int

        for (y in 0 until height) {
            lastX = -1
            firstX = lastX

            for (x in 0 until width) {
                if ((pixels[index].toInt() and 0xff) > MIN_VISIBLE_ALPHA) {
                    if (firstX == -1) {
                        firstX = x
                    }
                    lastX = x
                }
                index++
            }
            index += rowSizeDiff

            leftBorder[y] = firstX.toFloat()
            rightBorder[y] = lastX.toFloat()

            if (firstX != -1) {
                bottomY = y

                if (topY == -1) {
                    topY = y
                }

                leftX = min(firstX, leftX)
                rightX = max(lastX, rightX)
            }
        }

        if (topY == -1 || rightX == -1) {
            return 1f
        }

        convertToConvexArray(leftBorder, 1, topY, bottomY)
        convertToConvexArray(rightBorder, -1, topY, bottomY)

        var area = 0f
        for (y in 0 until height) {
            if (leftBorder[y] > -1) {
                area += rightBorder[y] - leftBorder[y] + 1
            }
        }

        val boundingArea = (bottomY + 1 - topY) * (rightX + 1 - leftX).toFloat()
        val hullByRect = area / boundingArea
        val scaleRequired = if (hullByRect < CIRCLE_AREA_BY_RECT) {
            MAX_CIRCLE_AREA_FACTOR
        } else {
            MAX_SQUARE_AREA_FACTOR + LINEAR_SCALE_SLOPE * (1 - hullByRect)
        }

        bounds.left = leftX
        bounds.right = rightX
        bounds.top = topY
        bounds.bottom = bottomY

        outBounds?.set(
            bounds.left.toFloat() / width,
            bounds.top.toFloat() / height,
            1 - (bounds.right.toFloat() / width),
            1 - (bounds.bottom.toFloat() / height)
        )

        if (outMaskShape != null && outMaskShape.isNotEmpty()) {
            outMaskShape[0] = isShape(path)
        }

        val areaScale = area / (width * height)
        val scale = if (areaScale > scaleRequired) sqrt(scaleRequired / areaScale) else 1f

        if (d is AdaptiveIconDrawable && adaptiveIconScale == SCALE_NOT_INITIALIZED) {
            adaptiveIconScale = scale
            adaptiveIconBounds.set(bounds)
        }

        return scale
    }

    private fun convertToConvexArray(
        xCoordinates: FloatArray,
        direction: Int,
        topY: Int,
        bottomY: Int
    ) {
        val total = xCoordinates.size
        val angles = FloatArray(total - 1)
        var last = -1
        var lastAngle = Float.MAX_VALUE

        for (i in (topY + 1)..bottomY) {
            if (xCoordinates[i] > -1) {
                var start = topY

                if (lastAngle != Float.MAX_VALUE) {
                    var currentAngle = (xCoordinates[i] - xCoordinates[last]) / (i - last)
                    start = last

                    if ((currentAngle - lastAngle) * direction < 0) {
                        while (start > topY) {
                            start--
                            currentAngle = (xCoordinates[i] - xCoordinates[start]) / (i - start)

                            if ((currentAngle - angles[start]) * direction >= 0) {
                                break
                            }
                        }
                    }
                }

                lastAngle = (xCoordinates[i] - xCoordinates[start]) / (i - start)

                for (j in start until i) {
                    angles[j] = lastAngle
                    xCoordinates[j] = xCoordinates[start] + lastAngle * (j - start)
                }

                last = i
            }
        }
    }

    private fun isShape(maskPath: Path?): Boolean {
        val iconRatio = bounds.width().toFloat() / bounds.height()
        if (abs(iconRatio - 1) > BOUND_RATIO_MARGIN) return false

        matrix.apply {
            reset()
            setScale(bounds.width().toFloat(), bounds.height().toFloat())
            postTranslate(bounds.left.toFloat(), bounds.top.toFloat())
        }

        maskPath?.transform(matrix, shapePath)

        canvas.drawPath(shapePath, paintMaskShape)
        canvas.drawPath(shapePath, paintMaskShapeOutline)

        return isTransparentBitmap()
    }

    private fun isTransparentBitmap(): Boolean {
        val buffer = ByteBuffer.wrap(pixels)
        buffer.rewind()
        bitmap.copyPixelsToBuffer(buffer)

        var index = bounds.top * maxSize
        val rowSizeDiff = maxSize - bounds.right
        var sum = 0

        for (y in bounds.top until bounds.bottom) {
            index += bounds.left
            for (x in bounds.left until bounds.right) {
                if ((pixels[index].toInt() and 0xff) > MIN_VISIBLE_ALPHA) {
                    sum++
                }
                index++
            }
            index += rowSizeDiff
        }

        val percentageDiffPixels = sum.toFloat() / (bounds.width() * bounds.height())
        return percentageDiffPixels < PIXEL_DIFF_PERCENTAGE_THRESHOLD
    }

    companion object {
        private const val SCALE_NOT_INITIALIZED = 0f
        private const val CIRCLE_AREA_BY_RECT = PI.toFloat() / 4
        private const val MAX_CIRCLE_AREA_FACTOR = 380f / 576
        private const val MAX_SQUARE_AREA_FACTOR = 375f / 576
        private const val LINEAR_SCALE_SLOPE =
            (MAX_CIRCLE_AREA_FACTOR - MAX_SQUARE_AREA_FACTOR) / (1 - CIRCLE_AREA_BY_RECT)

        private const val MIN_VISIBLE_ALPHA = 40
        private const val BOUND_RATIO_MARGIN = .05f
        private const val PIXEL_DIFF_PERCENTAGE_THRESHOLD = 0.005f
    }
}
