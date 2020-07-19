package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.requireHalauncherActivity

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    protected lateinit var toolbar: Toolbar
    protected val viewModel: SettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        requireHalauncherActivity().also { a ->
            a.setSupportActionBar(toolbar)
            a.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            a.exitFullscreen()
            a.setStatusBarTheme(false)
        }
    }

    protected fun <T : Preference> findPreference(@StringRes resId: Int): T? =
        findPreference(getString(resId))
}
