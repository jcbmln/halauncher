package xyz.mcmxciv.halauncher

import android.content.Context

class AppState private constructor(context: Context) {
    private val appModel: AppModel = AppModel(this)

    companion object {
        @Volatile private var instance: AppState? = null

        fun getInstance(context: Context): AppState {
            return instance ?: synchronized(this) {
                instance ?: AppState(context)
            }
        }
    }
}