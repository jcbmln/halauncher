package xyz.mcmxciv.halauncher.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.fragment_settings.*
import timber.log.Timber
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.components.ViewComponent
import xyz.mcmxciv.halauncher.views.ActionPreference
import xyz.mcmxciv.halauncher.views.ActionPreferenceDialogFragmentCompat
import java.lang.IllegalStateException

abstract class LauncherPreferenceFragment : PreferenceFragmentCompat() {
    protected val component: ViewComponent = LauncherApplication.instance.component
        .viewComponentBuilder().build()

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
            fragment?.show(requireActivity().supportFragmentManager,
                DIALOG_FRAGMENT_TAG
            )
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
