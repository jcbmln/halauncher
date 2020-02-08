package xyz.mcmxciv.halauncher.utils

import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import timber.log.Timber
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.components.FragmentComponent
import xyz.mcmxciv.halauncher.views.ActionPreference
import xyz.mcmxciv.halauncher.views.ActionPreferenceDialogFragmentCompat
import java.lang.IllegalStateException

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    protected val component: FragmentComponent =
        LauncherApplication.instance.component.fragmentBuilder().build()

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        var fragment: DialogFragment? = null

        if (preference is ActionPreference) {
            fragment = ActionPreferenceDialogFragmentCompat.newInstance(preference.key)
        }

        fragment?.setTargetFragment(this, 0)
        try {
            fragment?.show(requireActivity().supportFragmentManager, DIALOG_FRAGMENT_TAG)
        }
        catch (ex: IllegalStateException) {
            Timber.e(ex)
        }
    }

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"
    }
}
