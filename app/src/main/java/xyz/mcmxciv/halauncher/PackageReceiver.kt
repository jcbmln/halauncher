package xyz.mcmxciv.halauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> addPackage(intent.`package`)
            Intent.ACTION_PACKAGE_REMOVED -> {} //TODO: Remove package from database
            Intent.ACTION_PACKAGE_CHANGED -> {}
        }
    }

    private fun addPackage(intentPackage: String?) {}

    companion object {
        fun initialize() {

        }
    }
}