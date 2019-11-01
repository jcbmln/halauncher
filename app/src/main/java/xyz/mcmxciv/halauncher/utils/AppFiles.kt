package xyz.mcmxciv.halauncher.utils

import java.util.*

object AppFiles {
    private const val XML = ".xml"

    const val LAUNCHER_DB = "launcher.db"
    const val SHARED_PREFERENCES_KEY = "xyz.mcmxciv.halauncher.prefs"
    const val MANAGED_USER_PREFERENCES_KEY = "xyz.mcmxciv.halauncher.managedusers.prefs"

    // This preference file is not backed up to cloud.
    const val DEVICE_PREFERENCES_KEY = "xyz.mcmxciv.halauncher.device.prefs"

    const val APP_ICONS_DB = "app_icons.db"

    val ALL_FILES = Collections.unmodifiableList(
        listOf(
            LAUNCHER_DB,
            SHARED_PREFERENCES_KEY + XML,
            MANAGED_USER_PREFERENCES_KEY + XML,
            DEVICE_PREFERENCES_KEY + XML,
            APP_ICONS_DB
        )
    )
}