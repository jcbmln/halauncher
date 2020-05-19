package xyz.mcmxciv.halauncher.ui

import androidx.fragment.app.Fragment
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.components.ViewComponent

abstract class LauncherFragment : Fragment() {
    protected val component: ViewComponent = LauncherApplication.instance.component
        .viewComponentBuilder()
        .build()
}
