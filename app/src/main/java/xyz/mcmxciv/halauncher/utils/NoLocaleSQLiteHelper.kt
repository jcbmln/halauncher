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

package xyz.mcmxciv.halauncher.utils

import android.content.Context
import android.content.ContextWrapper
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.NO_LOCALIZED_COLLATORS
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build

/**
 * Extension of [SQLiteOpenHelper] which avoids creating default locale table by
 * A context wrapper which creates databases without support for localized collators.
 */
abstract class NoLocaleSQLiteHelper(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(if (ATLEAST_P) context else NoLocalContext(context), name, null, version) {

    init {
        if (ATLEAST_P) {
            setOpenParams(SQLiteDatabase.OpenParams.Builder().addOpenFlags(NO_LOCALIZED_COLLATORS).build())
        }
    }

    private class NoLocalContext(base: Context) : ContextWrapper(base) {

        override fun openOrCreateDatabase(
            name: String,
            mode: Int,
            factory: SQLiteDatabase.CursorFactory,
            errorHandler: DatabaseErrorHandler?
        ): SQLiteDatabase {
            return super.openOrCreateDatabase(
                name, mode or Context.MODE_NO_LOCALIZED_COLLATORS, factory, errorHandler
            )
        }
    }

    companion object {

        private val ATLEAST_P = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
}