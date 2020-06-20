package xyz.mcmxciv.halauncher.apps

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import xyz.mcmxciv.halauncher.utils.toBitmap

@Entity(tableName = "apps")
data class App(
    @PrimaryKey
    @ColumnInfo(name = "activity_name")
    val activityName: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "last_update")
    var lastUpdate: Long,
    @ColumnInfo(name = "system_app")
    val isSystemApp: Boolean,
    @ColumnInfo(name = "is_hidden")
    var isHidden: Boolean,
    @ColumnInfo(name = "icon", typeAffinity = ColumnInfo.BLOB)
    var icon: ByteArray
) {
    @Ignore
    val iconBitmap: Bitmap = icon.toBitmap()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as App

        if (activityName != other.activityName) return false
        if (packageName != other.packageName) return false
        if (displayName != other.displayName) return false
        if (lastUpdate != other.lastUpdate) return false
        if (isSystemApp != other.isSystemApp) return false
        if (!icon.contentEquals(other.icon)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = activityName.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + lastUpdate.hashCode()
        result = 31 * result + isSystemApp.hashCode()
        result = 31 * result + (icon.contentHashCode())
        return result
    }
}
