package xyz.mcmxciv.halauncher.utils

import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.components.FragmentComponent
import xyz.mcmxciv.halauncher.views.ActionPreference
import xyz.mcmxciv.halauncher.views.ActionPreferenceDialogFragmentCompat

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    protected val component: FragmentComponent =
        LauncherApplication.instance.component.fragmentBuilder().build()

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        var fragment: DialogFragment? = null

        if (preference is ActionPreference) {
            fragment = ActionPreferenceDialogFragmentCompat.newInstance(preference.key)
        }

        if (fragment != null) {
            fragment.setTargetFragment(this, 0)
            fragment.show(requireFragmentManager(), DIALOG_FRAGMENT_TAG)
        }
        else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"
    }
}
