package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName

data class AppDrawerItem(
    val app: App,
    val componentName: ComponentName,
    val shortcuts: List<Shortcut>
)
