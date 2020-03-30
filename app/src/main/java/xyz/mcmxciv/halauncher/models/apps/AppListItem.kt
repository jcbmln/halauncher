package xyz.mcmxciv.halauncher.models.apps

import android.content.ComponentName
import android.graphics.Bitmap
import xyz.mcmxciv.halauncher.data.models.App
import xyz.mcmxciv.halauncher.utils.toBitmap

data class AppListItem(
    val activityName: String,
    val packageName: String,
    val displayName: String,
    var lastUpdate: Long,
    val isSystemApp: Boolean,
    val isHidden: Boolean,
    val icon: Bitmap,
    var componentName: ComponentName,
    var shortcutItems: List<ShortcutItem>?
) {
    constructor(
        app: App,
        componentName: ComponentName,
        shortcutItems: List<ShortcutItem>?
    ) : this(
        app.activityName,
        app.packageName,
        app.displayName,
        app.lastUpdate,
        app.isSystemApp,
        app.isHidden,
        app.icon.toBitmap(),
        componentName,
        shortcutItems
    )
}