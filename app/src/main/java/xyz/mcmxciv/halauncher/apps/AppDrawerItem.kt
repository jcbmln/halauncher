package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName
import xyz.mcmxciv.halauncher.shortcuts.Shortcut

data class AppDrawerItem(
    val app: App,
    val componentName: ComponentName,
    val shortcuts: List<Shortcut>
)
