/**
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.ComponentName
import android.os.UserHandle

class ComponentKey(val componentName: ComponentName, val user: UserHandle) {
    private val _hashCode: Int = arrayOf<Any>(componentName, user).contentHashCode()

    override fun hashCode(): Int {
        return _hashCode
    }

    override fun equals(other: Any?): Boolean {
        val o = other as ComponentKey?
        return o!!.componentName == componentName && o.user == user
    }

    /**
     * Encodes a component key as a string of the form [flattenedComponentString#userId].
     */
    override fun toString(): String {
        return componentName.flattenToString() + "#" + user
    }
}