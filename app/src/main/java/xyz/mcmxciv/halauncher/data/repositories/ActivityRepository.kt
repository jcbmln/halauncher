package xyz.mcmxciv.halauncher.data.repositories

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.models.ActivityInfo
import javax.inject.Inject

class ActivityRepository @Inject constructor(
    private val context: Context,
    private val iconFactory: IconFactory
) {
    suspend fun getLaunchableActivities() : List<ActivityInfo> {
        val activities = getActivities()
        activities.sortBy { item -> item.displayName }

        return activities
    }

    private suspend fun getActivities(): ArrayList<ActivityInfo> {
        return withContext(Dispatchers.Default) {
            val activities = ArrayList<ActivityInfo>()
            val pm = context.packageManager
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveInfoList = pm.queryIntentActivities(launcherIntent, 0)

            for (resolveInfo in resolveInfoList) {
                activities.add(ActivityInfo(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.loadLabel(pm).toString(),
                    iconFactory.getIcon(resolveInfo.activityInfo)
                ))
            }

            return@withContext activities
        }
    }
}