package xyz.mcmxciv.halauncher.utils

import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String
    fun getSettingsString(name: String): String?
}