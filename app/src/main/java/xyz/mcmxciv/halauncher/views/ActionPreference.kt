package xyz.mcmxciv.halauncher.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import org.w3c.dom.Text
import xyz.mcmxciv.halauncher.R

class ActionPreference(
    context: Context, attrs: AttributeSet?, defAttrStyle: Int, defAttrRes: Int
) : Preference(context, attrs, defAttrStyle, defAttrRes) {
    private var textColor: ColorStateList?

    constructor(context: Context) : this(context, null)

    constructor(
        context: Context, attrs: AttributeSet?
    ) : this(context, attrs, R.attr.preferenceStyle)

    constructor(
        context: Context, attrs: AttributeSet?, defAttrStyle: Int
    ) : this(context, attrs, defAttrStyle, defAttrStyle)

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.ActionPreference, 0, 0
        ).apply {
            try {
                textColor = getColorStateList(R.styleable.ActionPreference_actionTextColor)
            }
            finally {
                recycle()
            }
        }

//        layoutResource = R.layout.action_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val textView = holder?.findViewById(android.R.id.title) as TextView?
        textView?.setTextColor(textColor)
    }
}