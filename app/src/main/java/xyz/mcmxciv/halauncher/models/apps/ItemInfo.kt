package xyz.mcmxciv.halauncher.models.apps

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap

abstract class ItemInfo {
    var iconBitmap: Bitmap? = null
    abstract val intent: Intent?
    open val targetComponent: ComponentName?
        get() = intent?.component
}