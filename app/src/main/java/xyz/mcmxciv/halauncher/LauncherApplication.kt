package xyz.mcmxciv.halauncher

import android.app.Application
import android.content.Context

class LauncherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        private lateinit var appContext: Context

        fun getAppContext(): Context = appContext
    }
}