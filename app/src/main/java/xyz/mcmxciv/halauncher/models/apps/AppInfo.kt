package xyz.mcmxciv.halauncher.models.apps

import android.content.ComponentName
import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import xyz.mcmxciv.halauncher.utils.toBitmap

@Entity(tableName = "app_info")
data class AppInfo(
    @PrimaryKey
    @ColumnInfo(name = "activity_name")
    val activityName: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "last_update")
    var lastUpdate: Long,
    @ColumnInfo(name = "icon", typeAffinity = ColumnInfo.BLOB)
    val iconBytes: ByteArray?
) {
    val icon: Bitmap?
        get() = iconBytes?.toBitmap()

    @Ignore
    var componentName: ComponentName? = null

    @Ignore
    var shortcutItems: List<ShortcutItem>? = null

    constructor(
        activityName: String,
        packageName: String,
        displayName: String,
        lastUpdate: Long,
        iconBytes: ByteArray?,
        componentName: ComponentName,
        shortcutItems: List<ShortcutItem>
    ) : this(activityName, packageName, displayName, lastUpdate, iconBytes) {
        this.componentName = componentName
        this.shortcutItems = shortcutItems
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppInfo

        if (activityName != other.activityName) return false
        if (packageName != other.packageName) return false
        if (displayName != other.displayName) return false
        if (lastUpdate != other.lastUpdate) return false
        if (iconBytes != null) {
            if (other.iconBytes == null) return false
            if (!iconBytes.contentEquals(other.iconBytes)) return false
        } else if (other.iconBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = activityName.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + lastUpdate.hashCode()
        result = 31 * result + (iconBytes?.contentHashCode() ?: 0)
        return result
    }
}