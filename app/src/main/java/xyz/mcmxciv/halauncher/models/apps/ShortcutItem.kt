package xyz.mcmxciv.halauncher.models.apps

import android.graphics.Bitmap

data class ShortcutItem(
    val shortcutId: String,
    val packageName: String,
    val displayName: String,
    val icon: Bitmap
)
