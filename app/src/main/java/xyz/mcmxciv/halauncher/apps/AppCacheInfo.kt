package xyz.mcmxciv.halauncher.apps

import android.content.pm.LauncherActivityInfo
import androidx.room.*
import xyz.mcmxciv.halauncher.utils.Converters
import java.time.Instant

@Entity(tableName = "app_cache_info")
data class AppCacheInfo(
    @PrimaryKey
    @ColumnInfo(name = "activity_name")
    val activityName: String,
    @ColumnInfo(name = "last_update")
    var lastUpdate: Instant,
    @ColumnInfo(name = "is_hidden")
    var isHidden: Boolean,
    @ColumnInfo(name = "order")
    var order: Int
)
