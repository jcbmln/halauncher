package xyz.mcmxciv.halauncher.models

import android.graphics.Bitmap

data class ActivityInfo(
    val packageName: String,
    val displayName: String,
    val icon: Bitmap
)