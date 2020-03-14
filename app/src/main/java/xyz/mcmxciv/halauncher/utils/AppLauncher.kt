package xyz.mcmxciv.halauncher.utils

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.view.View
import xyz.mcmxciv.halauncher.views.IconTextView
import javax.inject.Inject

class AppLauncher @Inject constructor(context: Context) {
    private val launcherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

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

    fun uninstall(componentName: ComponentName, context: Context) {
        val intent = Intent(Intent.ACTION_DELETE)
            .setData(Uri.fromParts(
                "package",
                componentName.packageName,
                componentName.className
            )).putExtra(Intent.EXTRA_USER, Process.myUserHandle())
        context.startActivity(intent)
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