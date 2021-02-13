package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName
import android.graphics.Bitmap
import xyz.mcmxciv.halauncher.shortcuts.Shortcut

data class App(
    val appCacheInfo: AppCacheInfo,
    val packageName: String,
    val displayName: String,
    val icon: Bitmap,
    val componentName: ComponentName,
    val isSystemApp: Boolean,
    val shortcuts: List<Shortcut>
)
