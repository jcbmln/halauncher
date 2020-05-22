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
}
