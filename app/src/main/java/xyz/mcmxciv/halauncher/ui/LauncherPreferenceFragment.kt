package xyz.mcmxciv.halauncher.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import timber.log.Timber
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.di.components.ViewComponent
import xyz.mcmxciv.halauncher.views.ActionPreference
import xyz.mcmxciv.halauncher.views.ActionPreferenceDialogFragmentCompat
import java.lang.IllegalStateException

abstract class LauncherPreferenceFragment : PreferenceFragmentCompat() {
    protected lateinit var toolbar: Toolbar
    protected val component: ViewComponent = LauncherApplication.instance.component
        .viewComponentBuilder().build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        activity?.let { fragmentActivity ->
            if (fragmentActivity is AppCompatActivity) {
                fragmentActivity.setSupportActionBar(toolbar)
                fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            is ActionPreference -> {
                val fragment = ActionPreferenceDialogFragmentCompat.newInstance(preference.key)
                fragment.setTargetFragment(this, 0)

                try {
                    fragment.show(parentFragmentManager, DIALOG_FRAGMENT_TAG)
                } catch (ex: IllegalStateException) { Timber.e(ex) }

            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    protected fun addClickListener(preference: Preference?, action: NavDirections) {
        preference?.setOnPreferenceClickListener {
            findNavController().navigate(action)
            return@setOnPreferenceClickListener true
        }
    }

    protected fun findPreference(@StringRes resId: Int): Preference? =
        findPreference(getString(resId))

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"
    }
}
