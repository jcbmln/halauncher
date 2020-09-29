package xyz.mcmxciv.halauncher.views

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.appcompat.widget.AppCompatImageView
import com.github.alexjlockwood.kyrie.KyrieDrawable
import xyz.mcmxciv.halauncher.R

// https://blog.stylingandroid.com/motion-vectors/
class SeekableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var kyrieDrawable: KyrieDrawable? = null

    init {
        if (drawable is AnimatedVectorDrawable) {
            @Suppress("CustomViewStyleable")
            context.obtainStyledAttributes(
                attrs,
                R.styleable.AppCompatImageView,
                defStyleAttr,
                0
            ).apply {
                setImageResource(
                    getResourceId(R.styleable.AppCompatImageView_srcCompat, -1)
                )
                recycle()
            }
        }
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        kyrieDrawable = KyrieDrawable.create(context, resId)?.also { setImageDrawable(it) }
    }

    @FloatRange(from = 0.0, to = 1.0)
    var seek: Float = 0f
        set(value) {
            field = kyrieDrawable?.run {
                currentPlayTime = (totalDuration.toFloat() * value).toLong()
                value
            } ?: 0f
        }
}
