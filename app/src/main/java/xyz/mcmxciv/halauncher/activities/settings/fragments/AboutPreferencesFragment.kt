package xyz.mcmxciv.halauncher.activities.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import xyz.mcmxciv.halauncher.R

class AboutPreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.sync_preferences, rootKey)
    }
}