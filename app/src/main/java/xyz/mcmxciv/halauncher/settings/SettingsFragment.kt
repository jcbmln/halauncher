package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.navigate
import xyz.mcmxciv.halauncher.observe
import java.util.prefs.PreferenceChangeListener

@AndroidEntryPoint
class SettingsFragment : BasePreferenceFragment() {
    private val navigationListener = Preference.OnPreferenceClickListener {
        viewModel.onCategorySelected(it.key)
        true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.settings)

        observe(viewModel.navigation) { navigate(it) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_screen_main)

        findPreference<Preference>(R.string.connection_settings_key)?.onPreferenceClickListener =
            navigationListener
        findPreference<Preference>(R.string.about_settings_key)?.onPreferenceClickListener =
            navigationListener
    }
}
