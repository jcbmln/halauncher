package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName
import android.graphics.Bitmap
import xyz.mcmxciv.halauncher.shortcuts.Shortcut

data class AppListItem(
    val app: App,
    val icon: Bitmap,
    val componentName: ComponentName,
    val shortcuts: List<Shortcut>
)
