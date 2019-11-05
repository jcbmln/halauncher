package xyz.mcmxciv.halauncher.models

import android.graphics.drawable.Drawable
import androidx.room.*

@Entity(tableName = "package_icons")
data class PackageIcon(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "icon") val icon: Drawable
)

@Dao
interface PackageIconDao {
    @Query("select * from package_icons")
    fun getAll(): List<PackageIcon>
}