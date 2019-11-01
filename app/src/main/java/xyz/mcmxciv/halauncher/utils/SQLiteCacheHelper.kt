package xyz.mcmxciv.halauncher.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * An extension of [SQLiteOpenHelper] with utility methods for a single table cache DB.
 * Any exception during write operations are ignored, and any version change causes a DB reset.
 */
abstract class SQLiteCacheHelper(
    context: Context,
    name: String,
    version: Int,
    private val tableName: String
) {
    private val openHelper: MySQLiteOpenHelper

    private var ignoreWrites: Boolean = false

    init {
        openHelper = MySQLiteOpenHelper(context, name, version)

        ignoreWrites = false
    }

    /**
     * @see SQLiteDatabase.delete
     */
    fun delete(whereClause: String, whereArgs: Array<String>) {
        if (ignoreWrites) {
            return
        }
        try {
            openHelper.writableDatabase.delete(tableName, whereClause, whereArgs)
        } catch (e: SQLiteFullException) {
            onDiskFull(e)
        } catch (e: SQLiteException) {
            Log.d(TAG, "Ignoring sqlite exception", e)
        }

    }

    /**
     * @see SQLiteDatabase.insertWithOnConflict
     */
    fun insertOrReplace(values: ContentValues) {
        if (ignoreWrites) {
            return
        }
        try {
            openHelper.writableDatabase.insertWithOnConflict(
                tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE
            )
        } catch (e: SQLiteFullException) {
            onDiskFull(e)
        } catch (e: SQLiteException) {
            Log.d(TAG, "Ignoring sqlite exception", e)
        }

    }

    private fun onDiskFull(e: SQLiteFullException) {
        Log.e(TAG, "Disk full, all write operations will be ignored", e)
        ignoreWrites = true
    }

    /**
     * @see SQLiteDatabase.query
     */
    fun query(columns: Array<String>, selection: String, selectionArgs: Array<String>): Cursor {
        return openHelper.readableDatabase.query(
            tableName, columns, selection, selectionArgs, null, null, null
        )
    }

    fun clear() {
        openHelper.clearDB(openHelper.writableDatabase)
    }

    fun close() {
        openHelper.close()
    }

    protected abstract fun onCreateTable(db: SQLiteDatabase)

    /**
     * A private inner class to prevent direct DB access.
     */
    private inner class MySQLiteOpenHelper(context: Context, name: String, version: Int) :
        NoLocaleSQLiteHelper(context, name, version) {

        override fun onCreate(db: SQLiteDatabase) {
            onCreateTable(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion != newVersion) {
                clearDB(db)
            }
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion != newVersion) {
                clearDB(db)
            }
        }

        fun clearDB(db: SQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS $tableName")
            onCreate(db)
        }
    }

    companion object {
        private const val TAG = "SQLiteCacheHelper"
    }
}