package xyz.mcmxciv.halauncher.apps

import android.graphics.Bitmap

data class Shortcut(
    val shortcutId: String,
    val packageName: String,
    val displayName: String,
    val icon: Bitmap
)
