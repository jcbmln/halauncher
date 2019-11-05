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
package xyz.mcmxciv.halauncher.interfaces

import android.content.Context
import android.text.TextUtils
import android.util.Log
import java.lang.reflect.InvocationTargetException

/**
 * An interface to indicate that a class is dynamically loaded using resource overlay, hence its
 * class name and constructor should be preserved by proguard
 */
interface ResourceBasedOverride {
    object Overrides {
        private val TAG = "Overrides"

        fun <T : ResourceBasedOverride> getObject(
            clazz: Class<T>, context: Context, resId: Int
        ): T {
            val className = context.getString(resId)
            if (!TextUtils.isEmpty(className)) {
                try {
                    val cls = Class.forName(className)
                    return cls.getDeclaredConstructor(Context::class.java).newInstance(context) as T
                } catch (e: ClassNotFoundException) {
                    Log.e(TAG, "Bad overriden class", e)
                } catch (e: InstantiationException) {
                    Log.e(TAG, "Bad overriden class", e)
                } catch (e: IllegalAccessException) {
                    Log.e(TAG, "Bad overriden class", e)
                } catch (e: ClassCastException) {
                    Log.e(TAG, "Bad overriden class", e)
                } catch (e: NoSuchMethodException) {
                    Log.e(TAG, "Bad overriden class", e)
                } catch (e: InvocationTargetException) {
                    Log.e(TAG, "Bad overriden class", e)
                }

            }

            try {
                return clazz.newInstance()
            } catch (e: InstantiationException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }

        }
    }
}