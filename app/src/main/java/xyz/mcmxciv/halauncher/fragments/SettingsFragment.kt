package xyz.mcmxciv.halauncher.fragments

import android.os.Bundle
import androidx.preference.*

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utilities.UserPreferences

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_main)

        bindPreferenceChangeListener(findPreference<EditTextPreference>(UserPreferences.HOME_ASSISTANT_KEY))
    }

    private fun bindPreferenceChangeListener(preference: Preference?) {
        preference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { pf, newValue ->
                val stringValue = newValue.toString()

                if (pf is EditTextPreference) {
                    pf.apply {
                        summary = stringValue
                        text = stringValue
                    }
                } else {
                    pf?.summary = stringValue
                }

                true
            }
    }
}
