package xyz.mcmxciv.halauncher.fragments

import android.os.Bundle
import androidx.preference.*

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utilities.UserSettings

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_main)

        bindPreferenceChangeListener(findPreference<EditTextPreference>(getString(R.string.key_home_assistant_url)))
        bindPreferenceChangeListener(findPreference<EditTextPreference>(getString(R.string.key_transparent_background)))
        bindPreferenceChangeListener(findPreference<EditTextPreference>(getString(R.string.key_blur_background)))
    }

    private fun bindPreferenceChangeListener(preference: Preference?) {
        preference?.onPreferenceChangeListener = PreferenceChangeListener
    }

    companion object PreferenceChangeListener : Preference.OnPreferenceChangeListener {
        private const val homeAssistantUrlKey = "key_home_assistant_url"
        private const val transparentBackgroundKey = "key_transparent_background"
        private const val blurBackgroundKey = "key_blur_background"

        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            val stringValue = newValue.toString()

            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(stringValue)
                preference.summary = if (index >= 0) preference.entries[index] else null
            }
            else if (preference is EditTextPreference) {
                if (preference.key == homeAssistantUrlKey) {
                    preference.summary = stringValue
                    UserSettings.url = stringValue
                }
            }
            else {
                preference?.summary = stringValue
            }

            return true
        }
    }
}
