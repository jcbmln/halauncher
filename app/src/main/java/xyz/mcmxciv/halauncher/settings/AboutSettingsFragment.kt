package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R

class AboutSettingsFragment : BasePreferenceFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.about_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_screen_about)

        findPreference<Preference>(R.string.app_version_key)?.also { pref ->
            pref.summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        }
        findPreference<Preference>(R.string.privacy_policy_key)?.also { pref ->
            pref.setOnPreferenceClickListener { viewModel.onPrivacyPolicySelected() }
        }
    }
}
