package xyz.mcmxciv.halauncher.models

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val displayName: String,
    val icon: Drawable
)

//class AppInfo {
//    lateinit var packageName: String
//    lateinit var displayName: String
//    lateinit var icon: Drawable
//}