package xyz.mcmxciv.halauncher.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import xyz.mcmxciv.halauncher.R

class ActionPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defAttrStyle: Int = 0,
    defAttrRes: Int = 0
) : DialogPreference(context, attrs, defAttrStyle, defAttrRes) {
    private var textColor: ColorStateList?
    var confirmationMessage: String?
    private val dialogLayoutResourceId = R.layout.dialog_confirmation

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.ActionPreference, 0, 0
        ).apply {
            try {
                textColor = getColorStateList(R.styleable.ActionPreference_actionTextColor)
                confirmationMessage = getString(R.styleable.ActionPreference_confirmationMessage)
            } finally {
                recycle()
            }
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val textView = holder?.findViewById(android.R.id.title) as TextView?
        textView?.setTextColor(textColor)
    }

    override fun getDialogLayoutResource(): Int {
        return dialogLayoutResourceId
    }

    fun doAction() {
        notifyChanged()
    }
}
