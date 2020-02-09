package xyz.mcmxciv.halauncher.utils

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.settings_fragment.*
import timber.log.Timber
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.di.components.FragmentComponent
import xyz.mcmxciv.halauncher.views.ActionPreference
import xyz.mcmxciv.halauncher.views.ActionPreferenceDialogFragmentCompat
import java.lang.IllegalStateException

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    protected val component: FragmentComponent =
        LauncherApplication.instance.component.fragmentBuilder().build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { fragmentActivity ->
            if (fragmentActivity is AppCompatActivity) {
                fragmentActivity.setSupportActionBar(toolbar)
                fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

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

    protected fun addClickListener(preference: Preference?, action: NavDirections) {
        preference?.setOnPreferenceClickListener {
            findNavController().navigate(action)
            true
        }
    }

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"
    }
}
