package xyz.mcmxciv.halauncher.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class PackageReceiver private constructor(
    intentFiler: IntentFilter,
    private val listener: PackageListener
) : BroadcastReceiver() {
    val filter = intentFiler

    override fun onReceive(context: Context, intent: Intent) {
        listener.onPackageReceived()
    }

    companion object {
        fun initialize(listener: PackageListener): PackageReceiver {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_PACKAGE_ADDED)
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED)

            filter.addDataScheme("package")
            return PackageReceiver(
                filter,
                listener
            )
        }
    }

    interface PackageListener {
        fun onPackageReceived()
//        fun onPackageAdded(packageName: String)
//        fun onPackageRemoved(packageName: String)
//        fun onPackageChanged(packageName: String)
    }
}
