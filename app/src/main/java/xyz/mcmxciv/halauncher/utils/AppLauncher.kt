package xyz.mcmxciv.halauncher.utils

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.view.View
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.views.IconTextView
import javax.inject.Inject

class AppLauncher @Inject constructor(resourceProvider: ResourceProvider) {
    private val launcherApps = resourceProvider.launcherApps

    fun startMainActivity(componentName: ComponentName, view: View) {
        launcherApps.startMainActivity(
            componentName,
            Process.myUserHandle(),
            view.getSourceBounds(),
            getActivityLaunchOptions(view)
        )
    }

    fun startAppDetailsActivity(componentName: ComponentName, view: View) {
        launcherApps.startAppDetailsActivity(
            componentName,
            Process.myUserHandle(),
            view.getSourceBounds(),
            getActivityLaunchOptions(view)
        )
    }

    fun startShortcut(packageName: String, shortcutId: String, view: View) {
        if (launcherApps.hasShortcutHostPermission()) {
            launcherApps.startShortcut(
                packageName,
                shortcutId,
                view.getSourceBounds(),
                null,
                Process.myUserHandle()
            )
        }
    }

    fun uninstall(componentName: ComponentName) {
        val intent = Intent(Intent.ACTION_DELETE)
            .setData(Uri.fromParts(
                "package",
                componentName.packageName,
                componentName.className
            )).putExtra(Intent.EXTRA_USER, Process.myUserHandle())
        LauncherApplication.instance.applicationContext.startActivity(intent)
    }

    private fun getActivityLaunchOptions(view: View): Bundle {
        var left = 0
        var top = 0
        var width: Int = view.measuredWidth
        var height: Int = view.measuredHeight

        if (view is IconTextView) {
            view.icon?.let { drawable ->
                val bounds = drawable.bounds
                left = (width - bounds.width()) / 2
                top = view.paddingTop
                width = bounds.width()
                height = bounds.height()
            }
        }

        return ActivityOptions.makeClipRevealAnimation(view, left, top, width, height).toBundle()
    }
}
