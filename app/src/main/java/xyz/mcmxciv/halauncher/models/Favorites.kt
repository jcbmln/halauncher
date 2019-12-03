package xyz.mcmxciv.halauncher.models

import androidx.room.*

@Entity(tableName = "favorites")
data class Favorites(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "cell") val cell: Int,
    @ColumnInfo(name = "page") val page: Int
)

@Dao
interface FavoritesDao {
    @Query("select * from favorites")
    fun get(): List<Favorites>

    @Insert
    fun insert(item: Favorites)

    @Delete
    fun delete(item: Favorites)
}