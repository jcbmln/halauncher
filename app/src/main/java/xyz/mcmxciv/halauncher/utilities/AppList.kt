package xyz.mcmxciv.halauncher.utilities

import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.Process
import org.xmlpull.v1.XmlPullParser
import java.util.HashMap
import kotlin.collections.ArrayList

class AppList {
    class AppInfo {
        lateinit var packageName: String
        lateinit var displayName: String
        var icon: Drawable? = null
    }

    companion object {
        fun getAppList(context: Context, invariantDeviceProfile: InvariantDeviceProfile):
                ArrayList<AppInfo> {
            val appList = ArrayList<AppInfo>()
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val activityList = launcherApps.getActivityList(null, Process.myUserHandle())
            val packageManager = context.packageManager
            val iconDpi = invariantDeviceProfile.fillResIconDpi

            for (item: LauncherActivityInfo in activityList) {
                appList.add(AppInfo().apply {
                    packageName = item.applicationInfo.packageName
                    displayName = packageManager.getApplicationLabel(item.applicationInfo).toString()
                    icon = getIcon(context, item, iconDpi)
                })
            }

            return appList
        }

        private fun getIcon(context: Context, activityInfo: LauncherActivityInfo,
                            iconDpi: Int): Drawable? {
            val applicationInfo = activityInfo.applicationInfo
            val resources = context.packageManager
                .getResourcesForApplication(applicationInfo.packageName)
            return getRoundIcon(context, activityInfo.componentName, iconDpi)
                ?: activityInfo.getIcon(iconDpi)
                ?: resources.getDrawableForDensity(android.R.drawable.sym_def_app_icon,
                    iconDpi, null)
        }

        private fun getRoundIcon(context: Context, component: ComponentName,
                                 iconDpi: Int): Drawable? {
            var appIcon: String? = null
            val elementTags = HashMap<String, String>()

            try {
                val resourcesForApplication =
                    context.packageManager.getResourcesForApplication(component.packageName)
                val assets = resourcesForApplication.assets

                val parseXml = assets.openXmlResourceParser("AndroidManifest.xml")
                while (parseXml.next() != XmlPullParser.END_DOCUMENT) {
                    if (parseXml.eventType == XmlPullParser.START_TAG) {
                        val name = parseXml.name
                        for (i in 0 until parseXml.attributeCount) {
                            elementTags[parseXml.getAttributeName(i)] =
                                parseXml.getAttributeValue(i)
                        }
                        if (elementTags.containsKey("icon")) {
                            if (name == "application") {
                                appIcon = elementTags["roundIcon"]
                            } else if ((name == "activity" || name == "activity-alias") &&
                                elementTags.containsKey("name") &&
                                elementTags["name"] == component.className
                            ) {
                                appIcon = elementTags["roundIcon"]
                                break
                            }
                        }
                        elementTags.clear()
                    }
                }
                parseXml.close()

                if (appIcon != null) {
                    val resId =
                        resourcesForApplication.getIdentifier(appIcon, null, component.packageName)
                    return resourcesForApplication.getDrawableForDensity(
                        if (resId == 0)
                            Integer.parseInt(appIcon.substring(1))
                        else
                            resId, iconDpi, null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }
}