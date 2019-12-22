package xyz.mcmxciv.halauncher.activities.settings.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.settings.SettingsViewModel
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BasePreferenceFragment

@Suppress("unused")
class AboutPreferencesFragment : BasePreferenceFragment() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.about_preferences, rootKey)

        val checkUpdatesPreference = findPreference<Preference>(CHECK_UPDATES_KEY)
        checkUpdatesPreference?.summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        checkUpdatesPreference?.setOnPreferenceClickListener {
            TODO("check for updates")
        }

        val privacyPolicyPreference = findPreference<Preference>(PRIVACY_POLICY_KEY)
        privacyPolicyPreference?.extras?.putString("url", BuildConfig.PRIVACY_POLICY_URL)
//        privacyPolicyPreference?.setOnPreferenceClickListener {
//            startActivity(Intent(
//                Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_POLICY_URL)
//            ))
//
//            true
//        }
    }

    companion object {
        private const val CHECK_UPDATES_KEY = "check_updates"
        private const val PRIVACY_POLICY_KEY = "privacy_policy"
    }
}
