/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.mcmxciv.halauncher.icons

import android.content.Context
import android.content.pm.ApplicationInfo
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Looper
import android.os.UserHandle
import xyz.mcmxciv.halauncher.utils.ComponentKey
import xyz.mcmxciv.halauncher.utils.SQLiteCacheHelper
import java.util.*
import kotlin.collections.HashMap

class IconCache(private val context: Context, private val dbFileName: String,
                private val looper: Looper, private val iconDpi: Int,
                private val iconPixelSize: Int, private val inMemoryCache: Boolean) {
    private val defaultIcons = HashMap<UserHandle, BitmapInfo>()
    private val packageManager = context.packageManager
    private val cache: Map<ComponentKey, CacheEntry>
    private val iconDB = IconDB(context, dbFileName, iconPixelSize)
    private var systemState = ""
    private var decodeOptions: BitmapFactory.Options? = null

    /**
     * Opens and returns an icon factory. The factory is recycled by the caller.
     */
    private var _iconFactory: IconFactory? = null
//    private val iconFactory: IconFactory
//        get() = _iconFactory ?: IconFactory(context, )

    init {
        cache = if (inMemoryCache) {
            HashMap(INITIAL_ICON_CACHE_CAPACITY)
        } else {
            object : AbstractMap<ComponentKey, CacheEntry>() {
                override val entries: MutableSet<MutableMap.MutableEntry<ComponentKey, CacheEntry>>
                    get() = Collections.emptySet()

                override fun put(key: ComponentKey, value: CacheEntry): CacheEntry? {
                    return value
                }
            }
        }

        if (BitmapRenderer.useHardwareBitmap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decodeOptions = BitmapFactory.Options()
            decodeOptions?.inPreferredConfig = Bitmap.Config.HARDWARE
        }

        updateSystemState()
    }

    /**
     * Returns the persistable serial number for {@param user}. Subclass should implement proper
     * caching strategy to avoid making binder call every time.
     */
    private fun getSerialNumberForUser(user: UserHandle): Int {
        return user.hashCode()
    }

    /**
     * Return true if the given app is an instant app and should be badged appropriately.
     */
    private fun isInstantApp(info: ApplicationInfo): Boolean {
        return false
    }

    /**
     * Refreshes the system state definition used to check the validity of the cache. It
     * incorporates all the properties that can affect the cache like locale and system-version.
     */
    private fun updateSystemState() {
        val locale = context.resources.configuration.locales.toLanguageTags()
        systemState = "$locale, ${Build.VERSION.SDK_INT}"
    }



    object CacheEntry : BitmapInfo() {
        var title: CharSequence = ""
        var contentDesciption: CharSequence = ""
    }

    companion object {
        private const val TAG = "IconCache"
        private const val INITIAL_ICON_CACHE_CAPACITY = 50

        // Empty class name is user for storing package default entry.
        const val EMPTY_CLASS_NAME = "."
    }

    internal class IconDB(context:Context, dbFileName:String, iconPixelSize:Int)
        : SQLiteCacheHelper(context,
            dbFileName,
            (RELEASE_VERSION shl 16) + iconPixelSize,
            TABLE_NAME
    ) {
        override fun onCreateTable(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_COMPONENT + " TEXT NOT NULL, " +
                COLUMN_USER + " INTEGER NOT NULL, " +
                COLUMN_LAST_UPDATED + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_VERSION + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_ICON + " BLOB, " +
                COLUMN_ICON_COLOR + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_LABEL + " TEXT, " +
                COLUMN_SYSTEM_STATE + " TEXT, " +
                "PRIMARY KEY (" + COLUMN_COMPONENT + ", " + COLUMN_USER + ") );"
            )
        }

        companion object {
            private const val RELEASE_VERSION = 26

            const val TABLE_NAME = "icons"
            const val COLUMN_COMPONENT = "componentName"
            const val COLUMN_USER = "profileId"
            const val COLUMN_LAST_UPDATED = "lastUpdated"
            const val COLUMN_VERSION = "version"
            const val COLUMN_ICON = "icon"
            const val COLUMN_ICON_COLOR = "icon_color"
            const val COLUMN_LABEL = "label"
            const val COLUMN_SYSTEM_STATE = "system_state"
        }
    }
}