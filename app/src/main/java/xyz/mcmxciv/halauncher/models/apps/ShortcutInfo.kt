package xyz.mcmxciv.halauncher.models.apps

import android.graphics.Bitmap

data class ShortcutInfo(
    val activityName: String,
    val packageName: String,
    val icon: Bitmap
)