package xyz.mcmxciv.halauncher.models

import android.graphics.Bitmap

data class AppInfo(
    val packageName: String,
    val displayName: String,
    val icon: Bitmap
)

//class AppInfo {
//    lateinit var packageName: String
//    lateinit var displayName: String
//    lateinit var icon: Drawable
//}