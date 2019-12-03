package xyz.mcmxciv.halauncher.utils

import android.content.Context

open class ContextInstance<out T: Any>(private val creator: (Context) -> T) {
    @Volatile private var _instance: T? = null

    fun getInstance(context: Context) = _instance ?: synchronized(this) {
        _instance ?: creator(context).also {
            _instance = it
        }
    }
}