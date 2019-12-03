package xyz.mcmxciv.halauncher

import android.content.Context

class SystemServiceInstance<out T: Any>(private val creator: Class<T>) {
    @Volatile private var _instance: T? = null

    fun getInstance(context: Context): Any = _instance ?: synchronized(this) {
        _instance ?: context.getSystemService(creator).also {
            _instance = it
        }
    }
}