package xyz.mcmxciv.halauncher.shortcuts

import android.graphics.Bitmap

data class Shortcut(
    val shortcutId: String,
    val packageName: String,
    val displayName: String,
    val icon: Bitmap
)
