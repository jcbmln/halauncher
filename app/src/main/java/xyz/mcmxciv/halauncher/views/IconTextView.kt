package xyz.mcmxciv.halauncher.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView

class IconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {
    private var _icon: Drawable? = null
    val icon: Drawable?
        get() = _icon

    var leftIcon: Drawable?
        get() = compoundDrawables[0]
        set(value) {
            _icon = value
            setCompoundDrawablesWithIntrinsicBounds(value, null, null, null)
        }

    var topIcon: Drawable?
        get() = compoundDrawables[1]
        set(value) {
            _icon = value
            setCompoundDrawablesWithIntrinsicBounds(null, value, null, null)
        }

    var rightIcon: Drawable?
        get() = compoundDrawables[2]
        set(value) {
            _icon = value
            setCompoundDrawablesWithIntrinsicBounds(null, null, value, null)
        }

    var bottomIcon: Drawable?
        get() = compoundDrawables[3]
        set(value) {
            _icon = value
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, value)
        }
}
