package xyz.mcmxciv.halauncher.models

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "activity_info")
data class ActivityInfo(
    @PrimaryKey
    @ColumnInfo(name = "activity_name")
    val activityName: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "last_update")
    var lastUpdate: Long,
    @ColumnInfo(name = "icon_file_name")
    val iconFileName: String
) {
    @Ignore
    lateinit var icon: Bitmap

    constructor(
        activityName: String,
        packageName: String,
        displayName: String,
        lastUpdate: Long,
        iconPath: String,
        bitmap: Bitmap
    ) : this(activityName, packageName, displayName, lastUpdate, iconPath) {
        icon = bitmap
    }
}