package xyz.mcmxciv.halauncher.views

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager
import com.google.android.material.textview.MaterialTextView
import xyz.mcmxciv.halauncher.utils.Utilities

class IconTextView(context: Context, attrs: AttributeSet?, defStyle: Int)
    : MaterialTextView(context, attrs, defStyle) {
    var icon: Drawable? = null

    var topIcon: Drawable?
        get() = icon ?: compoundDrawables[1]
        set(value) {
            icon = value
            setCompoundDrawablesWithIntrinsicBounds(null, value, null, null)
        }

    var leftIcon: Drawable?
        get() = icon ?: compoundDrawables[0]
        set(value) {
            icon = value
            setCompoundDrawablesWithIntrinsicBounds(value, null, null, null)
        }

    constructor(context: Context) : this(context, null , 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
}