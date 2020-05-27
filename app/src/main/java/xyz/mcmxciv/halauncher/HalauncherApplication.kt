package xyz.mcmxciv.halauncher

import android.app.Application

class HalauncherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: HalauncherApplication
            private set
    }
}
