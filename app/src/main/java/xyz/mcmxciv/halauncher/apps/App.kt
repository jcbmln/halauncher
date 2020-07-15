package xyz.mcmxciv.halauncher.apps

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
data class App(
    @PrimaryKey
    @ColumnInfo(name = "activity_name")
    val activityName: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "system_app")
    val isSystemApp: Boolean,
    @ColumnInfo(name = "is_hidden")
    var isHidden: Boolean,
    @Ignore
    val icon: Bitmap
) {
    constructor(
        activityName: String,
        packageName: String,
        displayName: String,
        isSystemApp: Boolean,
        isHidden: Boolean
    ) : this(
        activityName,
        packageName,
        displayName,
        isSystemApp,
        isHidden,
        Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
    )
}
