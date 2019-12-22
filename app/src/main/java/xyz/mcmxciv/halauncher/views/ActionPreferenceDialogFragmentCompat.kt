package xyz.mcmxciv.halauncher.views

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.preference.PreferenceDialogFragmentCompat
import xyz.mcmxciv.halauncher.R

class ActionPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        if (preference is ActionPreference) {
            val pref = preference as ActionPreference
            val textView = view?.findViewById<TextView>(android.R.id.edit)
            textView?.text = pref.confirmationMessage ?:
                    getString(R.string.default_confirmation_message)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (preference is ActionPreference) {
            preference.callChangeListener(positiveResult)
        }
    }

    companion object {
        fun newInstance(key: String): ActionPreferenceDialogFragmentCompat {
            val fragment = ActionPreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}
