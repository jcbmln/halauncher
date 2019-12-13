package xyz.mcmxciv.halauncher.utils

import androidx.preference.PreferenceFragmentCompat
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.components.FragmentComponent

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    protected val component: FragmentComponent =
        LauncherApplication.instance.component.fragmentBuilder().build()
}