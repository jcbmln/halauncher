package xyz.mcmxciv.halauncher.apps

import android.content.ComponentName
import android.content.pm.*
import android.os.Process
import android.os.UserHandle
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import xyz.mcmxciv.halauncher.icons.IconFactory
import xyz.mcmxciv.halauncher.shortcuts.Shortcut
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class AppManager @Inject constructor(
    private val appCacheInfoDao: AppCacheInfoDao,
    private val packageManager: PackageManager,
    private val launcherApps: LauncherApps,
    private val iconFactory: IconFactory
) : LauncherApps.Callback() {
    private val shortcutComparator = Comparator<ShortcutInfo> { a, b ->
        when {
            a.isDeclaredInManifest && !b.isDeclaredInManifest -> -1
            !a.isDeclaredInManifest && b.isDeclaredInManifest -> 1
            else -> a.rank.compareTo(b.rank)
        }
    }
    private val shortcutQuery = LauncherApps.ShortcutQuery()

    private val appCacheScope = CoroutineScope(Dispatchers.IO)
    private val appChannel = ConflatedBroadcastChannel<List<App>>()
    private val visibleAppChannel = ConflatedBroadcastChannel<List<App>>()
    private val _apps = mutableListOf<App>()

    @FlowPreview
    val apps: Flow<List<App>>
        get() = appChannel.asFlow()

    @FlowPreview
    val visibleApps: Flow<List<App>>
        get() = visibleAppChannel.asFlow()

    init {
        shortcutQuery.setQueryFlags(SHORTCUT_QUERY_FLAGS)
        appCacheScope.launch {
            val activities = launcherApps.getActivityList(null, Process.myUserHandle())
            val appCacheInfo = appCacheInfoDao.get()

            val newApps = activities.filter { a ->
                appCacheInfo.none { aa -> aa.activityName == a.name }
            }.mapNotNull { a -> populateFromActivity(a) }
            val cachedApps = appCacheInfo.mapNotNull { a -> populateFromCache(activities, a) }
            _apps.addAll(newApps)
            _apps.addAll(cachedApps)
            _apps.sortBy { a -> a.displayName }
            appChannel.offer(_apps)
            visibleAppChannel.offer(_apps.filter { a -> a.appCacheInfo.isVisible })
        }
    }

    fun toggleAppVisibility(activityName: String) {
        appCacheScope.launch {
            val appCacheInfo = appCacheInfoDao.get(activityName)
            appCacheInfo?.apply { isVisible = !isVisible }
            appCacheInfo?.also { appCacheInfoDao.update(it) }
        }
    }

    private suspend fun populateFromCache(
        activities: List<LauncherActivityInfo>,
        appCacheInfo: AppCacheInfo
    ): App? {
        val activity = activities.singleOrNull { a -> a.name == appCacheInfo.activityName }

        if (activity == null) {
            appCacheInfoDao.delete(appCacheInfo)
            return null
        }

        val isSystemApp = (activity.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1

        return App(
            appCacheInfo,
            activity.applicationInfo.packageName,
            activity.applicationInfo.loadLabel(packageManager).toString(),
            iconFactory.getIcon(activity),
            activity.componentName,
            isSystemApp,
            createShortcuts(activity.applicationInfo.packageName, activity.componentName)
        )
    }

    private suspend fun populateFromActivity(activity: LauncherActivityInfo): App {
        val packageInfo = packageManager.getPackageInfo(
            activity.applicationInfo.packageName,
            0
        )
        val isSystemApp = (activity.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1
        val appCacheInfo = AppCacheInfo(
            activity.name,
            Instant.now(),
            true,
            1
        )

        appCacheInfoDao.insert(appCacheInfo)

        return App(
            appCacheInfo,
            packageInfo.packageName,
            activity.applicationInfo.loadLabel(packageManager).toString(),
            iconFactory.getIcon(activity),
            activity.componentName,
            isSystemApp,
            createShortcuts(packageInfo.packageName, activity.componentName)
        )
    }


    private fun createShortcuts(packageName: String, componentName: ComponentName): List<Shortcut> {
        return if (launcherApps.hasShortcutHostPermission()) {
            queryShortcuts(packageName, componentName)
                .sortedWith(shortcutComparator)
                .take(4)
                .mapNotNull { s ->
                    iconFactory.getShortcutIcon(s)?.let {
                        Shortcut(
                            s.id,
                            packageName,
                            s.shortLabel!!.toString(),
                            it
                        )
                    }
                }
        } else listOf()
    }

    private fun queryShortcuts(
        packageName: String,
        componentName: ComponentName
    ): List<ShortcutInfo> {
        shortcutQuery.setPackage(packageName)
        shortcutQuery.setActivity(componentName)

        return launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle()) ?: listOf()
    }

    override fun onPackageRemoved(packageName: String?, user: UserHandle?) {
        TODO("Not yet implemented")
    }

    override fun onPackageAdded(packageName: String?, user: UserHandle?) {
        TODO("Not yet implemented")
    }

    override fun onPackageChanged(packageName: String?, user: UserHandle?) {
        TODO("Not yet implemented")
    }

    override fun onPackagesAvailable(
        packageNames: Array<out String>?,
        user: UserHandle?,
        replacing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun onPackagesUnavailable(
        packageNames: Array<out String>?,
        user: UserHandle?,
        replacing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val SHORTCUT_QUERY_FLAGS = LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
            LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC
    }
}
